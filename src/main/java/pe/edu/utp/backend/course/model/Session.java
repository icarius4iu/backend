package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.content.model.Content;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"week", "contents"})
@EqualsAndHashCode(exclude = {"week", "contents"})
public class Session {



    public enum SessionType {
        TEORIA("Teoría"),
        PRACTICA("Práctica"),
        LABORATORIO("Laboratorio"),
        EVALUACION("Evaluación"),
        TALLER("Taller");

        private final String displayName;

        SessionType(String displayName) {
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
    @JoinColumn(name = "week_id", nullable = false)
    private Week week;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    private String buildingName;
    private String roomNumber;

    private String meetingUrl;
    private String meetingId;
    private String meetingPassword;

    @OneToMany(mappedBy = "session")
    private List<Content> contents = new ArrayList<>();

    // Cambios aquí: Modalidad ya no está en Course, sino en Section.
    public boolean isPresencial() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getModality() == Section.Modality.PRESENCIAL;
    }

    public boolean isVirtualVivo() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getModality() == Section.Modality.VIRTUAL_VIVO;
    }

    public boolean isVirtual247() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getModality() == Section.Modality.VIRTUAL_24_7;
    }
    public int getSessionNumber() {
        if (week == null || week.getSessions() == null) {
            return 0;
        }
        List<Session> orderedSessions = week.getSessions().stream()
                .sorted(Comparator.comparing(Session::getSessionDate)) // o por startTime, como necesites
                .toList();

        for (int i = 0; i < orderedSessions.size(); i++) {
            if (orderedSessions.get(i).getId().equals(this.id)) {
                return i + 1;
            }
        }
        return 0;
    }
}