package pe.edu.utp.backend.schedule.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;


import pe.edu.utp.backend.course.service.core.SectionService;
import pe.edu.utp.backend.schedule.dtos.ScheduleConflictDTO;
import pe.edu.utp.backend.schedule.dtos.ScheduleDTO;
import pe.edu.utp.backend.schedule.dtos.StudentScheduleDTO;
import pe.edu.utp.backend.schedule.mapper.ScheduleDTOMapper;
import pe.edu.utp.backend.schedule.model.ScheduleAlert;
import pe.edu.utp.backend.schedule.model.ScheduleEntry;
import pe.edu.utp.backend.schedule.model.StudentSchedule;
import pe.edu.utp.backend.schedule.repository.ScheduleAlertRepository;
import pe.edu.utp.backend.schedule.repository.ScheduleEntryRepository;
import pe.edu.utp.backend.schedule.repository.StudentScheduleRepository;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.service.StudentService;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final StudentScheduleRepository scheduleRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;

    private final ScheduleEntryRepository entryRepository;
    private final StudentService studentService;
    private final ScheduleDTOMapper scheduleDTOMapper;

    // Añadido: SectionService para resolver el error de compilación
    @Autowired
    @Lazy private final SectionService sectionService;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private ScheduleAlertRepository scheduleAlertRepository;



    /**
     * Obtiene o crea un horario para un estudiante
     */
    public StudentSchedule getOrCreateSchedule(Long studentId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        return scheduleRepository.findByStudent(student)
                .orElseGet(() -> {
                    StudentSchedule newSchedule = StudentSchedule.builder()
                            .student(student)
                            .lastUpdated(LocalDateTime.now())
                            .notificationEnabled(true)
                            .build();
                    return scheduleRepository.save(newSchedule);
                });
    }

    /**
     * Actualiza el horario basado en las secciones matriculadas
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StudentSchedule updateScheduleFromSections(Long studentId) {
        try {
            // PASO 1: Obtener el estudiante con sus secciones inicializadas
            Student student = studentService.findStudentWithSections(studentId)
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

            // PASO 2: Eliminar entradas existentes
            System.out.println("[" + LocalDateTime.now() + "] Eliminando entradas para estudiante: " + studentId);

            entityManager.createQuery(
                            "DELETE FROM ScheduleEntry e WHERE e.studentSchedule.student.id = :studentId")
                    .setParameter("studentId", studentId)
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            // PASO 3: Obtener o crear horario limpio
            StudentSchedule schedule = scheduleRepository.findByStudent(student)
                    .orElseGet(() -> {
                        StudentSchedule newSchedule = new StudentSchedule();
                        newSchedule.setStudent(student);
                        newSchedule.setNotificationEnabled(true);
                        newSchedule.setLastUpdated(LocalDateTime.now());
                        return scheduleRepository.save(newSchedule);
                    });

            if (schedule.getEntries() == null) {
                schedule.setEntries(new HashSet<>());
            } else {
                schedule.getEntries().clear();
            }

            schedule = scheduleRepository.save(schedule);
            entityManager.flush();

            // PASO 4: Crear entradas para cada sección
            int entriesCreated = 0;

            // La clave es obtener y procesar las secciones dentro de esta transacción
            for (Section section : sectionService.findSectionsWithWeeksAndSessionsByStudentId(studentId)) {
                if (section.getCicle() == null) {
                    System.out.println("⚠️ Sección sin ciclo: " + section.getCode());
                    continue;
                }

                LocalDate cicleStartDate = section.getCicle().getStartDate();
                LocalDate cicleEndDate = section.getCicle().getEndDate();

                if (section.getWeeks() == null || section.getWeeks().isEmpty()) {
                    System.out.println("⚠️ Sección sin semanas: " + section.getCode());
                    continue;
                }

                List<Week> orderedWeeks = new ArrayList<>(section.getWeeks());
                orderedWeeks.sort(Comparator.comparing(Week::getWeekNumber));

                for (Week week : orderedWeeks) {
                    if (week.getSessions() == null || week.getSessions().isEmpty()) {
                        continue;
                    }

                    for (Session session : week.getSessions()) {
                        // Calcular fecha
                        LocalDate sessionDate = calculateSessionDate(session, week.getWeekNumber()-1, cicleStartDate);

                        if (sessionDate.isAfter(cicleEndDate)) {
                            continue;
                        }

                        // Crear nueva entrada usando constructor en lugar de builder
                        ScheduleEntry entry = new ScheduleEntry();
                        entry.setStudentSchedule(schedule);
                        entry.setSection(section);
                        entry.setSession(session);

                        entry.setTitle(section.getCourse().getName() + " - " + session.getTitle());
                        entry.setDescription(session.getDescription());
                        entry.setDate(sessionDate);
                        entry.setDayOfWeek(session.getDayOfWeek());
                        entry.setStartTime(session.getStartTime());
                        entry.setEndTime(session.getEndTime());

                        // Ubicación
                        String location = "";
                        if (session.getBuildingName() != null) location += session.getBuildingName();
                        if (session.getRoomNumber() != null) {
                            if (!location.isEmpty()) location += " - ";
                            location += session.getRoomNumber();
                        }
                        entry.setLocation(location);

                        entry.setMeetingUrl(section.getVirtualMeetingUrl());
                        entry.setEntryType(mapSessionTypeToEntryType(session.getType()));

                        boolean isEvaluation = session.getTitle() != null &&
                                (session.getTitle().toLowerCase().contains("examen") ||
                                        session.getTitle().toLowerCase().contains("evaluación") ||
                                        session.getTitle().toLowerCase().contains("trabajo final"));
                        entry.setHasEvaluation(isEvaluation);

                        entry.setCourseCode(section.getCourse().getCode());
                        entry.setCourseName(section.getCourse().getName());
                        entry.setSectionCode(section.getCode());

                        entry.setCompleted(false);
                        entry.setReminded(false);

                        // Guardar entrada
                        entryRepository.save(entry);
                        schedule.addEntry(entry);
                        entriesCreated++;
                    }
                }
            }

            schedule.updateLastUpdated();
            System.out.println("✅ Generadas " + entriesCreated + " entradas");

            return scheduleRepository.save(schedule);
        } catch (Exception e) {
            System.err.println("❌ ERROR en updateScheduleFromSections: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Filtra entradas posteriores al TRABAJO FINAL
     */
    private Set<ScheduleEntry> removeEntriesAfterFinalWork(Set<ScheduleEntry> scheduleEntries) {
        if (scheduleEntries == null || scheduleEntries.isEmpty()) {
            return scheduleEntries;
        }

        // Buscar la entrada de TRABAJO FINAL
        Optional<ScheduleEntry> finalWorkEntry = scheduleEntries.stream()
                .filter(entry -> entry.getTitle() != null &&
                        entry.getTitle().toUpperCase().contains("TRABAJO FINAL"))
                .findFirst();

        // Si no hay entrada de TRABAJO FINAL, retornar todas las entradas
        if (!finalWorkEntry.isPresent()) {
            return scheduleEntries;
        }

        // Obtener la fecha del TRABAJO FINAL
        LocalDate finalWorkDate = finalWorkEntry.get().getDate();
        System.out.println("🎓 Encontrado TRABAJO FINAL en fecha: " + finalWorkDate);

        // Filtrar las entradas para mantener solo las anteriores o iguales al TRABAJO FINAL
        Set<ScheduleEntry> filteredEntries = scheduleEntries.stream()
                .filter(entry -> {
                    // Mantener la entrada del TRABAJO FINAL
                    if (entry.equals(finalWorkEntry.get())) {
                        return true;
                    }

                    // Para otras entradas, verificar que sean anteriores o el mismo día
                    return entry.getDate() != null &&
                            (entry.getDate().isBefore(finalWorkDate) ||
                                    entry.getDate().isEqual(finalWorkDate));
                })
                .collect(Collectors.toSet());

        System.out.println("🧹 Filtradas " + (scheduleEntries.size() - filteredEntries.size()) +
                " entradas posteriores al TRABAJO FINAL");

        return filteredEntries;
    }

    /**
     * Calcula la fecha real de una sesión basada en el día de la semana y el inicio del ciclo
     */
    private LocalDate calculateSessionDate(Session session, int weekIndex, LocalDate cicleStartDate) {
        try {
            // Si no hay día de la semana, usar lunes por defecto
            DayOfWeek dayOfWeek = session.getDayOfWeek();
            if (dayOfWeek == null) {
                System.out.println("  ⚠️ Sesión sin día de la semana definido, usando LUNES");
                dayOfWeek = DayOfWeek.MONDAY;
            }

            // Obtener el lunes de la semana de inicio del ciclo
            LocalDate cycleStartMonday = cicleStartDate;
            while (cycleStartMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
                cycleStartMonday = cycleStartMonday.minusDays(1);
            }

            // Calcular la fecha de la sesión sumando las semanas y ajustando al día
            LocalDate baseWeekStart = cycleStartMonday.plusWeeks(weekIndex);
            LocalDate sessionDate = baseWeekStart;

            // Ajustar al día de la semana correcto
            while (sessionDate.getDayOfWeek() != dayOfWeek) {
                sessionDate = sessionDate.plusDays(1);
            }

            return sessionDate;
        } catch (Exception e) {
            System.err.println("  ❌ Error calculando fecha de sesión: " + e.getMessage());
            // Retornar una fecha por defecto si hay error
            return cicleStartDate.plusWeeks(weekIndex);
        }
    }

    /**
     * Obtiene el horario para un rango de fechas, filtrando entradas después del TRABAJO FINAL
     */
    @Transactional(readOnly = true)
    public StudentScheduleDTO getScheduleForDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Buscar el horario con todas las entradas
        StudentSchedule schedule = scheduleRepository.findByStudent(student)
                .orElse(new StudentSchedule());

        // Si no hay entradas, retornar el horario vacío
        if (schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            return scheduleDTOMapper.toDTO(schedule);
        }

        // PRIMERO: Filtrar entradas posteriores a TRABAJO FINAL
        Set<ScheduleEntry> filteredByFinalWork = removeEntriesAfterFinalWork(schedule.getEntries());

        // SEGUNDO: Si hay fechas, filtrar por rango
        if (startDate != null && endDate != null) {
            filteredByFinalWork = filteredByFinalWork.stream()
                    .filter(entry -> entry.getDate() != null &&
                            !entry.getDate().isBefore(startDate) &&
                            !entry.getDate().isAfter(endDate))
                    .collect(Collectors.toCollection(HashSet::new));
        }

        // Crear copia del horario con entradas filtradas
        StudentSchedule filtered = new StudentSchedule();
        filtered.setId(schedule.getId());
        filtered.setStudent(schedule.getStudent());
        filtered.setLastUpdated(schedule.getLastUpdated());
        filtered.setNotificationEnabled(schedule.getNotificationEnabled());
        filtered.setEntries(filteredByFinalWork);

        // Convertir a DTO
        return scheduleDTOMapper.toDTO(filtered);
    }

    /**
     * Obtiene el horario aplicando filtro de TRABAJO FINAL
     */
    @Transactional(readOnly = true)
    public StudentScheduleDTO getScheduleWithFinalWorkFilter(Long studentId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        StudentSchedule schedule = scheduleRepository.findByStudent(student)
                .orElse(new StudentSchedule());

        // Si no hay entradas, retornar el horario vacío
        if (schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            return scheduleDTOMapper.toDTO(schedule);
        }

        // Filtrar entradas posteriores a TRABAJO FINAL
        Set<ScheduleEntry> filteredByFinalWork = removeEntriesAfterFinalWork(schedule.getEntries());

        // Crear copia del horario con entradas filtradas
        StudentSchedule filtered = new StudentSchedule();
        filtered.setId(schedule.getId());
        filtered.setStudent(schedule.getStudent());
        filtered.setLastUpdated(schedule.getLastUpdated());
        filtered.setNotificationEnabled(schedule.getNotificationEnabled());
        filtered.setEntries(filteredByFinalWork);

        return scheduleDTOMapper.toDTO(filtered);
    }

    /**
     * Detecta conflictos al matricular en una nueva sección
     * @return Lista de conflictos o lista vacía si no hay problemas
     */
    public List<ScheduleConflictDTO> checkScheduleConflicts(Long studentId, Long sectionId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Obtener las entradas actuales de horario
        StudentSchedule schedule = scheduleRepository.findByStudent(student)
                .orElse(new StudentSchedule());

        // Buscar la sección en la que se quiere matricular
        Section newSection = sectionService.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        List<ScheduleConflictDTO> conflicts = new ArrayList<>();

        // Iterar por cada sesión de la nueva sección
        for (Week week : newSection.getWeeks()) {
            for (Session newSession : week.getSessions()) {
                // Para cada sesión existente, verificar conflictos
                for (ScheduleEntry existingEntry : schedule.getEntries()) {
                    if (existingEntry.getDayOfWeek() == newSession.getDayOfWeek()) {
                        // Verificar superposición de horarios
                        if (hasTimeOverlap(existingEntry, newSession)) {
                            conflicts.add(createConflictDTO(existingEntry, newSession, "OVERLAP"));
                        }
                        // Verificar proximidad (menos de 15 minutos entre clases)
                        else if (hasProximityIssue(existingEntry, newSession, 15)) {
                            conflicts.add(createConflictDTO(existingEntry, newSession, "PROXIMITY_15MIN"));
                        }
                    }
                }
            }
        }

        return conflicts;
    }

    /**
     * Obtiene el horario semanal de un estudiante
     */
    public ScheduleDTO getWeeklySchedule(Long studentId, LocalDate startDate) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Ajustar a lunes de la semana
        LocalDate monday = startDate.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        StudentSchedule schedule = scheduleRepository
                .findByStudentWithEntriesInDateRange(studentId, monday, sunday)
                .orElseGet(() -> scheduleRepository.findByStudent(student).orElse(new StudentSchedule()));

        return createScheduleDTO(schedule, monday, sunday);
    }

    /**
     * Mapea el tipo de sesión al tipo de entrada
     */
    private ScheduleEntry.EntryType mapSessionTypeToEntryType(Session.SessionType sessionType) {
        if (sessionType == null) {
            return ScheduleEntry.EntryType.CLASS_SESSION; // Valor por defecto
        }

        switch (sessionType) {
            case TEORIA: return ScheduleEntry.EntryType.CLASS_SESSION;
            case PRACTICA: return ScheduleEntry.EntryType.LAB;
            case EVALUACION: return ScheduleEntry.EntryType.EXAM;
            default: return ScheduleEntry.EntryType.CLASS_SESSION;
        }
    }

    /**
     * Verifica si hay superposición de horarios entre una entrada existente y una nueva sesión
     */
    private boolean hasTimeOverlap(ScheduleEntry entry, Session session) {
        return !entry.getEndTime().isBefore(session.getStartTime()) &&
                !entry.getStartTime().isAfter(session.getEndTime());
    }

    /**
     * Verifica si hay problemas de proximidad entre horarios (menos de X minutos entre clases)
     */
    private boolean hasProximityIssue(ScheduleEntry entry, Session session, int minutesThreshold) {
        Duration duration = Duration.between(entry.getEndTime(), session.getStartTime());
        return Math.abs(duration.toMinutes()) < minutesThreshold;
    }

    /**
     * Crea un DTO con la información del conflicto
     */
    private ScheduleConflictDTO createConflictDTO(ScheduleEntry existing, Session newSession, String type) {
        return ScheduleConflictDTO.builder()
                .existingCourse(existing.getSection().getCourse().getName())
                .existingSection(existing.getSection().getCode())
                .existingTime(existing.getStartTime() + " - " + existing.getEndTime())
                .newSession(newSession.getTitle())
                .newTime(newSession.getStartTime() + " - " + newSession.getEndTime())
                .day(existing.getDayOfWeek().toString())
                .conflictType(type)
                .build();
    }

    /**
     * Crea un DTO con la información del horario semanal
     */
    private ScheduleDTO createScheduleDTO(StudentSchedule schedule, LocalDate startDate, LocalDate endDate) {
        // Implementación para convertir las entradas del horario en un DTO estructurado
        // Organizado por días y horas
        return ScheduleDTO.builder()
                .studentId(schedule.getStudent().getId())
                .studentName(schedule.getStudent().getFullName())
                .startDate(startDate)
                .endDate(endDate)
                // Aquí procesarías las entradas por día/hora
                .build();
    }

    @Transactional
    public void reprogramScheduleEntry(
            Long scheduleEntryId,
            LocalDate newDate,
            LocalTime newStartTime,
            LocalTime newEndTime,
            String newRoomNumber,
            String reason
    ) {
        ScheduleEntry entry = scheduleEntryRepository.findById(scheduleEntryId)
                .orElseThrow(() -> new NoSuchElementException("Schedule entry not found"));

        if (newDate != null) entry.setDate(newDate);
        if (newStartTime != null) entry.setStartTime(newStartTime);
        if (newEndTime != null) entry.setEndTime(newEndTime);

        if (entry.getSection() != null && entry.getSection().getModality() != null
                && entry.getSection().getModality() == Section.Modality.PRESENCIAL) {
            entry.setLocation(newRoomNumber);
        }

        scheduleEntryRepository.save(entry);

        // Notificar a todos los estudiantes de la sección
        Long sectionId = entry.getSection().getId();
        String message = "La clase '" + entry.getTitle() + "' ha sido reprogramada para el " +
                (newDate != null ? newDate : entry.getDate()) +
                " de " + (newStartTime != null ? newStartTime : entry.getStartTime()) +
                " a " + (newEndTime != null ? newEndTime : entry.getEndTime()) +
                (newRoomNumber != null ? " en el aula/lab: " + newRoomNumber : "") +
                ". Motivo: " + reason;
        notifySectionStudents(sectionId, message);
    }
    public void notifySectionStudents(Long sectionId, String message) {
        List<Student> students = studentService.findStudentsBySection(sectionId);
        for (Student student : students) {
            ScheduleAlert alert = new ScheduleAlert();
            alert.setStudent(student);
            alert.setMessage(message);
            alert.setCreatedAt(LocalDateTime.now());
            scheduleAlertRepository.save(alert);
            // Aquí puedes agregar lógica para email, push, etc.
        }
    }
}