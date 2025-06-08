package pe.edu.utp.backend.course.dto.request;

import lombok.Data;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Section;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
public class DetailedSectionCreationRequest {
    private String code;
    private Long courseId;
    private Long cicleId;
    private Integer maxStudents;
    private Integer weeksCount;
    private Integer sessionsPerWeek;
    private String virtualMeetingUrl;

    private Section.Modality modality; // Nuevo campo para modalidad

    private SessionScheduleConfig theorySession;
    private SessionScheduleConfig labSession;
    private List<SpecialSession> specialSessions;

    // Clases para configuración
    @Data
    public static class SessionScheduleConfig {
        private Session.SessionType type; // TEORIA o LABORATORIO
        private String defaultTitle;      // Título por defecto
        private String description;       // Descripción general
        private List<ScheduleDay> scheduleDays; // Días en que se dicta (lunes 6:30-8, miércoles 8:15-9:45, etc.)
        private String buildingName;      // Edificio por defecto
        private String roomNumber;        // Aula por defecto
    }

    @Data
    public static class ScheduleDay {
        private DayOfWeek dayOfWeek;      // MONDAY, WEDNESDAY, etc.
        private LocalTime startTime;       // 06:30
        private LocalTime endTime;         // 08:00
    }

    @Data
    public static class SpecialSession {
        private Integer weekNumber;        // Número de semana (5, 10, 16)
        private Integer sessionNumber;     // 1 (teoría) o 2 (lab)
        private String title;              // "AVANCE DE TRABAJO FINAL 1"
        private String description;        // Descripción específica
    }

}