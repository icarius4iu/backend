package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.content.model.Content;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "weeks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"section", "sessions", "contents"})
@EqualsAndHashCode(exclude = {"section", "sessions", "contents"})
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private Integer weekNumber;

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    // Cambiado a Set para evitar MultipleBagFetchException
    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Session> sessions = new HashSet<>();

    @OneToMany(mappedBy = "week")
    private Set<Content> contents = new HashSet<>();

    public void addSession(Session session) {
        sessions.add(session);
        session.setWeek(this);
    }

    public void removeSession(Session session) {
        if (sessions != null) {
            sessions.remove(session);
            session.setWeek(null);
        }
    }
}