package pe.edu.utp.backend.schedule.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_entries")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"studentSchedule", "section", "session"})
@EqualsAndHashCode(of = {"id"})
public class ScheduleEntry {



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

    @JsonIgnoreProperties("entries")
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

    // Nuevos campos para mejorar la visualización
    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "section_code")
    private String sectionCode;



    public boolean isEvaluation() {
        return entryType == EntryType.EXAM ||
                (hasEvaluation != null && hasEvaluation);
    }

    /**
     * Genera información de ubicación completa según la modalidad de la sección.
     */
    public String getFullLocation() {
        if (section != null && section.getModality() != null) {
            switch (section.getModality()) {
                case PRESENCIAL:
                case VIRTUAL_VIVO:
                    return location;
                case VIRTUAL_24_7:
                    return meetingUrl;
            }
        }
        return location != null ? location : meetingUrl;
    }
    public void setSessionDate(LocalDate newDate) {
        this.date = newDate;
    }
}