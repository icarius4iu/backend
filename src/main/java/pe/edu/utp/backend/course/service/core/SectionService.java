package pe.edu.utp.backend.course.service.core;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.course.dto.SectionCreateDTO;
import pe.edu.utp.backend.course.dto.request.ScheduleDayDTO;
import pe.edu.utp.backend.course.dto.request.SectionCreateAdvancedDTO;
import pe.edu.utp.backend.course.dto.request.SessionConfigDTO;
import pe.edu.utp.backend.course.dto.request.SpecialSessionDTO;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.*;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;
import pe.edu.utp.backend.schedule.service.ScheduleService;
import pe.edu.utp.backend.storage.service.FirebaseStorageService;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.service.StudentService;
import pe.edu.utp.backend.util.cicle.model.Cicle;
import pe.edu.utp.backend.util.cicle.repository.CicleRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SectionService extends BaseAcademicService<Section, Long> {

    private final CourseRepository courseRepository;
    private static final Logger logger = Logger.getLogger(SectionService.class.getName());
    private final StudentService studentService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    @Lazy
    private ScheduleService scheduleService;

    @Autowired
    private CicleRepository cicleRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private WeekService weekService;
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository, StudentService studentService) {
        super(sectionRepository);
        this.courseRepository = courseRepository;
        this.studentService = studentService;
    }

    public SectionRepository getSectionRepository() {
        return (SectionRepository) repository;
    }

    @Transactional
    public Section createCompleteSection(SectionCreateDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new InvalidAcademicEntityException("No se encontró el curso con ID: " + dto.getCourseId()));
        Cicle cicle = cicleRepository.findById(dto.getCicleId())
                .orElseThrow(() -> new InvalidAcademicEntityException("No se encontró el ciclo con ID: " + dto.getCicleId()));

        Section section = Section.builder()
                .code(dto.getCode())
                .course(course)
                .cicle(cicle)
                .maxStudents(dto.getMaxStudents())
                .virtualMeetingUrl(dto.getVirtualMeetingUrl())
                .modality(dto.getModality()) // <-- AGREGA ESTA LÍNEA
                .build();

        section = save(section);

        if (dto.getWeeksCount() != null && dto.getWeeksCount() > 0) {
            section.generateWeeks(dto.getWeeksCount(), dto.getSessionsPerWeek());
        }

        return save(section);
    }

    @Transactional
    public Section addProfessorToSection(Long sectionId, Professor professor) {
        Section section = getById(sectionId);
        section.addProfessor(professor);
        return save(section);
    }

    @Transactional
    public Map<String, Object> addStudentsToSection(Long sectionId, List<Long> studentIds) {
        Map<String, Object> result = new HashMap<>();
        List<Long> successIds = new ArrayList<>();
        Map<Long, String> failedIds = new HashMap<>();

        try {
            Section section = repository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

            Object[] counts = (Object[]) entityManager.createNativeQuery(
                            "SELECT COUNT(*), s.max_students FROM sections s " +
                                    "LEFT JOIN section_student ss ON s.id = ss.section_id " +
                                    "WHERE s.id = ? GROUP BY s.max_students")
                    .setParameter(1, sectionId)
                    .getSingleResult();

            int currentEnrolled = ((Number) counts[0]).intValue();
            int maxStudents = ((Number) counts[1]).intValue();
            int available = maxStudents - currentEnrolled;

            if (available < studentIds.size()) {
                throw new RuntimeException("No hay suficientes cupos. Disponibles: " + available);
            }

            for (Long studentId : studentIds) {
                try {
                    Long count = (Long) entityManager.createQuery(
                                    "SELECT COUNT(s) FROM Section sec JOIN sec.students s " +
                                            "WHERE sec.id = :sectionId AND s.id = :studentId")
                            .setParameter("sectionId", sectionId)
                            .setParameter("studentId", studentId)
                            .getSingleResult();

                    if (count > 0) {
                        failedIds.put(studentId, "Ya está matriculado");
                        continue;
                    }

                    boolean studentExists = (boolean) entityManager.createQuery(
                                    "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.id = :id")
                            .setParameter("id", studentId)
                            .getSingleResult();

                    if (!studentExists) {
                        failedIds.put(studentId, "Estudiante no encontrado");
                        continue;
                    }

                    int insertedRows = entityManager.createNativeQuery(
                                    "INSERT INTO section_student (section_id, student_id) VALUES (?, ?)")
                            .setParameter(1, sectionId)
                            .setParameter(2, studentId)
                            .executeUpdate();

                    if (insertedRows > 0) {
                        successIds.add(studentId);
                    }

                } catch (Exception e) {
                    failedIds.put(studentId, e.getMessage());
                }
            }

            result.put("sectionId", sectionId);
            result.put("sectionCode", section.getCode());
            result.put("successful", successIds);
            result.put("failed", failedIds);
            result.put("successCount", successIds.size());
            result.put("failCount", failedIds.size());
            result.put("currentEnrolled", currentEnrolled + successIds.size());
            result.put("maxCapacity", maxStudents);

            if (!successIds.isEmpty()) {
                for (Long studentId : successIds) {
                    scheduleService.updateScheduleFromSections(studentId);
                }
                System.out.println("Se actualizaron los horarios de " + successIds.size() + " estudiantes");
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Transactional
    public Section createAdvancedSection(SectionCreateAdvancedDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new InvalidAcademicEntityException("No se encontró el curso con ID: " + dto.getCourseId()));
        Cicle cicle = cicleRepository.findById(dto.getCicleId())
                .orElseThrow(() -> new InvalidAcademicEntityException("No se encontró el ciclo con ID: " + dto.getCicleId()));

        int weeksCount = cicle.getWeeksCount();
        if (weeksCount <= 0) {
            throw new InvalidAcademicEntityException("El ciclo debe tener un número de semanas mayor a cero");
        }

        Section section = Section.builder()
                .code(dto.getCode())
                .course(course)
                .cicle(cicle)
                .maxStudents(dto.getMaxStudents())
                .virtualMeetingUrl(dto.getVirtualMeetingUrl())
                .modality(dto.getModality()) // <-- AGREGA ESTA LÍNEA
                .build();

        section = save(section);
        generateAdvancedWeeks(section, dto, weeksCount);
        section = save(section);
        generateAdvancedWeeks(section, dto, weeksCount);

        // NUEVO: Genera estructura de carpetas en Firebase Storage
        createSectionFoldersInStorage(section);

        return save(section);
    }

    // Nuevo método para crear carpetas en storage
    private void createSectionFoldersInStorage(Section section) {
        Course course = section.getCourse();
        String baseSectionPath = String.format(
                "courses/%s_%s/sections/%s/",
                course.getCode(), sanitize(course.getName()), section.getCode()
        );

        // Por cada semana
        for (Week week : section.getWeeks()) {
            String weekPath = baseSectionPath + "week_" + week.getWeekNumber() + "/";
            firebaseStorageService.createFolderIfNotExists(weekPath);

            // Por cada sesión en la semana
            int sessionNum = 1;
            for (Session session : week.getSessions()) {
                String sessionPath = weekPath + "session_" + sessionNum + "/";
                firebaseStorageService.createFolderIfNotExists(sessionPath);
                sessionNum++;
            }
        }
    }

    // Utilidad para limpiar el nombre
    private String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    private void generateAdvancedWeeks(Section section, SectionCreateAdvancedDTO dto, int weeksCount) {
        Cicle cicle = section.getCicle();
        LocalDate cycleStartDate = cicle.getStartDate();

        System.out.println("Fecha inicio del ciclo: " + cycleStartDate + ", Semanas totales: " + weeksCount);

        for (int i = 0; i < weeksCount; i++) {
            int weekNumber = i + 1;
            LocalDate weekStartDate = cycleStartDate.plusWeeks(i);
            LocalDate weekEndDate = weekStartDate.plusDays(6);

            System.out.println("Generando Semana " + weekNumber + " (" + weekStartDate + " a " + weekEndDate + ")");

            Week week = Week.builder()
                    .weekNumber(weekNumber)
                    .section(section)
                    .startDate(weekStartDate)
                    .endDate(weekEndDate)
                    .title("Semana " + weekNumber)
                    .build();

            week = weekService.save(week);

            if (dto.getTheorySession() != null && dto.getTheorySession().getScheduleDays() != null) {
                generateSessionsForConfig(week, dto.getTheorySession(), weekNumber, weekStartDate);
            }
            if (dto.getLabSession() != null && dto.getLabSession().getScheduleDays() != null) {
                generateSessionsForConfig(week, dto.getLabSession(), weekNumber, weekStartDate);
            }
            section.addWeek(week);
        }

        applyAllSpecialSessions(section, dto.getSpecialSessions(), weeksCount);
    }

    private List<Session> generateSessionsForConfig(Week week, SessionConfigDTO config, int weekNumber, LocalDate weekStartDate) {
        List<Session> sessions = new ArrayList<>();
        for (ScheduleDayDTO scheduleDay : config.getScheduleDays()) {
            LocalDate sessionDate = getSessionDate(weekStartDate, scheduleDay.getDayOfWeek());
            Session session = Session.builder()
                    .title(config.getDefaultTitle() + " - Semana " + weekNumber)
                    .description(config.getDescription())
                    .type(config.getType())
                    .dayOfWeek(scheduleDay.getDayOfWeek())
                    .sessionDate(sessionDate)
                    .startTime(scheduleDay.getStartTime())
                    .endTime(scheduleDay.getEndTime())
                    .buildingName(config.getBuildingName())
                    .roomNumber(config.getRoomNumber())
                    .week(week)
                    .build();

            week.addSession(session);
            sessions.add(session);
        }
        return sessions;
    }

    private void applyAllSpecialSessions(Section section, List<SpecialSessionDTO> specialSessions, int weeksCount) {
        if (specialSessions == null || specialSessions.isEmpty()) return;

        int totalWeeks = section.getCicle().getWeeksCount();

        Optional<SpecialSessionDTO> trabajoFinalOpt = specialSessions.stream()
                .filter(s -> "TRABAJO FINAL".equals(s.getTitle()))
                .findFirst();

        if (!trabajoFinalOpt.isPresent()) {
            applyNormalSpecialSessions(section, specialSessions, totalWeeks);
            return;
        }

        SpecialSessionDTO trabajoFinal = trabajoFinalOpt.get();
        int finalWeekNumber = trabajoFinal.getWeekNumber() != null ?
                trabajoFinal.getWeekNumber() : totalWeeks;

        List<SpecialSessionDTO> previousSpecials = specialSessions.stream()
                .filter(s -> !s.equals(trabajoFinal) &&
                        (s.getWeekNumber() == null || s.getWeekNumber() < finalWeekNumber))
                .collect(Collectors.toList());

        applyNormalSpecialSessions(section, previousSpecials, finalWeekNumber - 1);

        List<Week> orderedWeeks = section.getWeeks().stream()
                .sorted(Comparator.comparing(Week::getWeekNumber))
                .collect(Collectors.toList());
        Optional<Week> finalWeekOpt = orderedWeeks.stream()
                .filter(w -> w.getWeekNumber() == finalWeekNumber)
                .findFirst();

        if (finalWeekOpt.isPresent()) {
            Week finalWeek = finalWeekOpt.get();

            if (!finalWeek.getSessions().isEmpty()) {
                List<Session> sessionList = new ArrayList<>(finalWeek.getSessions());
                Session finalSession = sessionList.get(0);
                finalSession.setTitle(trabajoFinal.getTitle());
                finalSession.setDescription(trabajoFinal.getDescription());

                sessionList.remove(finalSession);
                for (Session session : sessionList) {
                    finalWeek.removeSession(session);
                }
                System.out.println("TRABAJO FINAL aplicado en semana " + finalWeekNumber +
                        ". Eliminadas " + sessionList.size() + " sesiones adicionales.");
            }
            List<Week> weeksToRemove = new ArrayList<>();
            for (Week week : section.getWeeks()) {
                if (week.getWeekNumber() > finalWeekNumber) {
                    weeksToRemove.add(week);
                }
            }
            for (Week week : weeksToRemove) {
                section.removeWeek(week);
                weekService.deleteById(week.getId());
            }
            System.out.println("Eliminadas " + weeksToRemove.size() +
                    " semanas posteriores al TRABAJO FINAL.");
        }
    }
    private void applyNormalSpecialSessions(Section section, List<SpecialSessionDTO> specials, int maxWeeks) {
        if (specials == null || specials.isEmpty()) return;

        List<Week> orderedWeeks = section.getWeeks().stream()
                .filter(w -> w.getWeekNumber() <= maxWeeks)
                .sorted(Comparator.comparing(Week::getWeekNumber))
                .collect(Collectors.toList());

        for (SpecialSessionDTO special : specials) {
            if (special.getWeekNumber() > 0 && special.getWeekNumber() <= maxWeeks) {
                Optional<Week> weekOpt = orderedWeeks.stream()
                        .filter(w -> w.getWeekNumber() == special.getWeekNumber())
                        .findFirst();

                if (weekOpt.isPresent()) {
                    Week week = weekOpt.get();
                    List<Session> sessionList = new ArrayList<>(week.getSessions());
                    if (!sessionList.isEmpty() && sessionList.size() >= special.getSessionNumber()) {
                        Session sessionToModify = sessionList.get(special.getSessionNumber() - 1);
                        sessionToModify.setTitle(special.getTitle());
                        sessionToModify.setDescription(special.getDescription());
                        System.out.println("Sesión especial " + special.getTitle() +
                                " aplicada en semana " + week.getWeekNumber() +
                                ", sesión " + special.getSessionNumber());
                    }
                }
            }
        }
    }

    private LocalDate getSessionDate(LocalDate weekStart, java.time.DayOfWeek targetDay) {
        LocalDate date = weekStart;
        while (date.getDayOfWeek() != targetDay) {
            date = date.plusDays(1);
            if (date.isAfter(weekStart.plusDays(6))) {
                date = weekStart.plusWeeks(1);
                System.out.println("Advertencia: No se encontró el día " + targetDay +
                        " en la semana que comienza el " + weekStart);
                break;
            }
        }
        return date;
    }

    @Transactional
    public Section generateWeeks(Long sectionId) {
        Section section = getById(sectionId);
        int weeksCount = section.getCicle().getWeeksCount();
        int sessionsPerWeek = 2;
        section.generateWeeks(weeksCount, sessionsPerWeek);
        return save(section);
    }

    private void validateSectionDates(Section section) {
        System.out.println("Validando fechas para sección " + section.getCode());
        List<Week> weeks = new ArrayList<>(section.getWeeks());
        for (Week week : weeks) {
            System.out.println("Semana " + week.getWeekNumber() +
                    ": " + week.getStartDate() + " a " + week.getEndDate());
            List<Session> sessionList = new ArrayList<>(week.getSessions());
            for (Session session : sessionList) {
                if (session.getSessionDate() == null) {
                    System.err.println("ERROR: Sesión sin fecha en semana " + week.getWeekNumber());
                } else if (session.getSessionDate().isBefore(week.getStartDate()) ||
                        session.getSessionDate().isAfter(week.getEndDate())) {
                    System.err.println("ERROR: Sesión con fecha " + session.getSessionDate() +
                            " fuera del rango de la semana " + week.getWeekNumber() +
                            " (" + week.getStartDate() + " a " + week.getEndDate() + ")");
                }
            }
        }
    }

    public List<Section> findSectionsByCourse(Course course) {
        return getSectionRepository().findByCourse(course);
    }

    public List<Section> findSectionsByProfessor(Professor professor) {
        return getSectionRepository().findByProfessorsContaining(professor);
    }

    public List<Section> findSectionsByStudent(Student student) {
        return getSectionRepository().findByStudentsContaining(student);
    }

    public List<Section> findSectionsWithAvailableSeats() {
        return getSectionRepository().findSectionsWithAvailableSeats();
    }

    @Transactional(readOnly = true)
    public List<Section> findSectionsWithWeeksAndSessionsByStudentId(Long studentId) {
        return sectionRepository.findSectionsWithWeeksAndSessionsByStudentId(studentId);
    }
}