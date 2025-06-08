package pe.edu.utp.backend.course.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.backend.course.dto.SectionCreateDTO;
import pe.edu.utp.backend.course.dto.request.DetailedSectionCreationRequest;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.service.core.CourseService;
import pe.edu.utp.backend.course.service.core.SectionService;
import pe.edu.utp.backend.course.service.core.WeekService;
import pe.edu.utp.backend.course.service.core.SessionService;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SectionCreationService {
    private final CourseService courseService;
    private final SectionService sectionService;
    private final WeekService weekService;
    private final SessionService sessionService;

    /**
     * Crea una sección detallada con todas sus semanas y sesiones
     */
    public Section createDetailedSection(DetailedSectionCreationRequest req) {
        Section section = createBasicSection(req); // sección ya persistida, con Cicle asignado
        int totalWeeks = section.getCicle().getWeeksCount(); // <-- aquí obtienes el valor correcto del ciclo

        SpecialSessionConfig specialConfig = extractSpecialConfig(req, totalWeeks);
        generateWeeksAndSessions(section, req, specialConfig);

        return section;
    }

    /**
     * Crea la sección básica asociada al curso
     */
    private Section createBasicSection(DetailedSectionCreationRequest req) {
        SectionCreateDTO dto = new SectionCreateDTO();
        dto.setCode(req.getCode());
        dto.setCourseId(req.getCourseId());
        dto.setCicleId(req.getCicleId());
        dto.setMaxStudents(req.getMaxStudents());
        dto.setWeeksCount(req.getWeeksCount());
        dto.setSessionsPerWeek(req.getSessionsPerWeek());
        dto.setVirtualMeetingUrl(req.getVirtualMeetingUrl());
        dto.setModality(req.getModality()); // <-- AGREGA ESTA LÍNEA

        return sectionService.createCompleteSection(dto);
    }

    /**
     * Extrae la configuración de sesiones especiales del DTO
     */
    // Recibe el Section o el totalWeeks real como parámetro
    private SpecialSessionConfig extractSpecialConfig(DetailedSectionCreationRequest req, int totalWeeks) {
        Map<Integer, Map<Integer, DetailedSectionCreationRequest.SpecialSession>> specialSessionsMap = new HashMap<>();
        Integer finalWeekNumber = null;

        if (req.getSpecialSessions() != null) {
            for (DetailedSectionCreationRequest.SpecialSession specialSession : req.getSpecialSessions()) {
                // Si no trae weekNumber (como TRABAJO FINAL), se asigna a la última semana
                Integer wk = specialSession.getWeekNumber() != null ? specialSession.getWeekNumber() : totalWeeks;
                // Si no trae sessionNumber, lo ponemos como teoría (1)
                Integer sn = specialSession.getSessionNumber() != null ? specialSession.getSessionNumber() : 1;

                specialSessionsMap.computeIfAbsent(wk, k -> new HashMap<>())
                        .put(sn, specialSession);

                if ("TRABAJO FINAL".equals(specialSession.getTitle())) {
                    finalWeekNumber = wk;
                }
            }
        }

        int safeFinalWeekNumber = (finalWeekNumber != null) ? finalWeekNumber : totalWeeks;
        return new SpecialSessionConfig(safeFinalWeekNumber, specialSessionsMap);
    }

    /**
     * Genera todas las semanas y sesiones para la sección
     */
    private void generateWeeksAndSessions(Section section,
                                          DetailedSectionCreationRequest req,
                                          SpecialSessionConfig specialConfig) {
        // Fecha inicial para calcular semanas
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Crear cada semana hasta la semana final
        for (int weekNumber = 1; weekNumber <= specialConfig.getFinalWeekNumber(); weekNumber++) {
            Week week = createWeek(section, weekNumber, startDate);

            boolean isTrabajoFinalWeek = isTrabajoFinalWeek(weekNumber, specialConfig);

            // Crear sesiones para esta semana
            createTheorySessions(week, req, weekNumber, isTrabajoFinalWeek, specialConfig);
            createLabSessions(week, req, weekNumber, isTrabajoFinalWeek, specialConfig);
        }
    }

    /**
     * Crea una semana para la sección
     */
    private Week createWeek(Section section, int weekNumber, LocalDate startDate) {
        LocalDate weekStartDate = startDate.plusWeeks(weekNumber - 1);
        LocalDate weekEndDate = weekStartDate.plusDays(6);

        Week week = Week.builder()
                .weekNumber(weekNumber)
                .title("Semana " + weekNumber)
                .section(section)
                .startDate(weekStartDate)
                .endDate(weekEndDate)
                .build();

        return weekService.createWeek(week);
    }

    /**
     * Determina si la semana contiene el TRABAJO FINAL
     */
    private boolean isTrabajoFinalWeek(int weekNumber, SpecialSessionConfig config) {
        return config.getSpecialSessionsMap().containsKey(weekNumber) &&
                config.getSpecialSessionsMap().get(weekNumber).containsKey(1) &&
                "TRABAJO FINAL".equals(config.getSpecialSessionsMap()
                        .get(weekNumber).get(1).getTitle());
    }

    /**
     * Crea las sesiones teóricas para una semana
     */
    private void createTheorySessions(
            Week week,
            DetailedSectionCreationRequest req,
            int weekNumber,
            boolean isTrabajoFinalWeek,
            SpecialSessionConfig config
    ) {
        if (req.getTheorySession() == null || req.getTheorySession().getScheduleDays() == null) {
            return;
        }

        // Si es semana de TRABAJO FINAL, genera SOLO la sesión especial
        if (isTrabajoFinalWeek) {
            // Busca la sesión especial TRABAJO FINAL en el mapa
            DetailedSectionCreationRequest.SpecialSession trabajoFinal = config.getSpecialSessionsMap()
                    .getOrDefault(weekNumber, new HashMap<>())
                    .get(1); // 1 = teoría

            String title = (trabajoFinal != null && trabajoFinal.getTitle() != null) ? trabajoFinal.getTitle() : "TRABAJO FINAL";
            String description = (trabajoFinal != null && trabajoFinal.getDescription() != null)
                    ? trabajoFinal.getDescription() : "Entrega final del trabajo del curso";

            // Usa el primer día del calendario de teoría
            DetailedSectionCreationRequest.ScheduleDay scheduleDay = req.getTheorySession().getScheduleDays().get(0);
            LocalDate sessionDate = week.getStartDate()
                    .with(TemporalAdjusters.nextOrSame(scheduleDay.getDayOfWeek()));

            Session session = Session.builder()
                    .week(week)
                    .title(title)
                    .description(description)
                    .type(req.getTheorySession().getType())
                    .sessionDate(sessionDate)
                    .dayOfWeek(scheduleDay.getDayOfWeek())
                    .startTime(scheduleDay.getStartTime())
                    .endTime(scheduleDay.getEndTime())
                    .buildingName(req.getTheorySession().getBuildingName())
                    .roomNumber(req.getTheorySession().getRoomNumber())
                    .build();

            sessionService.createSession(session);
            return;
        }

        // Si no es semana de TRABAJO FINAL, genera las sesiones regulares
        int theorySessionNumber = 1;
        boolean isFirstTheorySession = true;
        for (DetailedSectionCreationRequest.ScheduleDay scheduleDay : req.getTheorySession().getScheduleDays()) {
            LocalDate sessionDate = week.getStartDate()
                    .with(TemporalAdjusters.nextOrSame(scheduleDay.getDayOfWeek()));

            String defaultTitle = "Clase teórica #" + theorySessionNumber + " - Semana " + weekNumber;
            String title = defaultTitle;
            String description = req.getTheorySession().getDescription();

            // Verifica si hay sesión especial para esta semana
            if (config.getSpecialSessionsMap().containsKey(weekNumber) &&
                    config.getSpecialSessionsMap().get(weekNumber).containsKey(1)) {
                DetailedSectionCreationRequest.SpecialSession special =
                        config.getSpecialSessionsMap().get(weekNumber).get(1);
                title = special.getTitle();
                description = special.getDescription() != null ?
                        special.getDescription() : "Entrega final del trabajo del curso";
            }

            Session session = Session.builder()
                    .week(week)
                    .title(title)
                    .description(description)
                    .type(req.getTheorySession().getType())
                    .sessionDate(sessionDate)
                    .dayOfWeek(scheduleDay.getDayOfWeek())
                    .startTime(scheduleDay.getStartTime())
                    .endTime(scheduleDay.getEndTime())
                    .buildingName(req.getTheorySession().getBuildingName())
                    .roomNumber(req.getTheorySession().getRoomNumber())
                    .build();

            sessionService.createSession(session);

            theorySessionNumber++;
            isFirstTheorySession = false;
        }
    }

    /**
     * Crea las sesiones de laboratorio para una semana
     */
    private void createLabSessions(Week week,
                                   DetailedSectionCreationRequest req,
                                   int weekNumber,
                                   boolean isTrabajoFinalWeek,
                                   SpecialSessionConfig config) {
        // No crear laboratorios en semana de TRABAJO FINAL
        if (isTrabajoFinalWeek || req.getLabSession() == null || req.getLabSession().getScheduleDays() == null) {
            return;
        }

        int labSessionNumber = 1;

        for (DetailedSectionCreationRequest.ScheduleDay scheduleDay : req.getLabSession().getScheduleDays()) {
            LocalDate sessionDate = week.getStartDate()
                    .with(TemporalAdjusters.nextOrSame(scheduleDay.getDayOfWeek()));

            // Configurar título y descripción
            String defaultTitle = "Laboratorio #" + labSessionNumber + " - Semana " + weekNumber;
            String title = defaultTitle;
            String description = req.getLabSession().getDescription();

            // Verificar si hay sesión especial
            if (config.getSpecialSessionsMap().containsKey(weekNumber) &&
                    config.getSpecialSessionsMap().get(weekNumber).containsKey(2)) {
                DetailedSectionCreationRequest.SpecialSession special =
                        config.getSpecialSessionsMap().get(weekNumber).get(2);
                title = special.getTitle();
                description = special.getDescription() != null ?
                        special.getDescription() : description;
            }

            // Crear la sesión
            Session session = Session.builder()
                    .week(week)
                    .title(title)
                    .description(description)
                    .type(req.getLabSession().getType())
                    .sessionDate(sessionDate)
                    .dayOfWeek(scheduleDay.getDayOfWeek())
                    .startTime(scheduleDay.getStartTime())
                    .endTime(scheduleDay.getEndTime())
                    .buildingName(req.getLabSession().getBuildingName())
                    .roomNumber(req.getLabSession().getRoomNumber())
                    .build();

            sessionService.createSession(session);
            labSessionNumber++;
        }
    }

    /**
     * Clase interna para mantener la configuración de sesiones especiales
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class SpecialSessionConfig {
        private final int finalWeekNumber;
        private final Map<Integer, Map<Integer, DetailedSectionCreationRequest.SpecialSession>> specialSessionsMap;
    }
}