package pe.edu.utp.backend.schedule.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntry {

    /**
     * Tipos de eventos en el horario
     */
    public enum EntryType {
        CLASS_SESSION("Sesión de clase"),
        EXAM("Evaluación"),
        LAB("Laboratorio"),
        WORKSHOP("Taller"),
        DEADLINE("Fecha límite de entrega"),
        PERSONAL("Evento personal");

        private final String displayName;

        EntryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_schedule_id", nullable = false)
    private StudentSchedule studentSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private String location;

    private String meetingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;

    @Column(name = "has_evaluation")
    private Boolean hasEvaluation;

    @Column(name = "evaluation_weight")
    private Double evaluationWeight;

    @Column(name = "is_completed")
    private Boolean completed;

    @Column(name = "is_reminded")
    private Boolean reminded;

    /**
     * Determina si el evento es una evaluación
     */
    public boolean isEvaluation() {
        return entryType == EntryType.EXAM ||
                (hasEvaluation != null && hasEvaluation);
    }

    /**
     * Genera información de ubicación completa
     */
    public String getFullLocation() {
        if (section != null && section.getCourse() != null) {
            if (section.getCourse().isRealTimeAttendance()) {
                return location;
            } else {
                return meetingUrl;
            }
        }
        return location != null ? location : meetingUrl;
    }
}