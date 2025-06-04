package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.content.model.Content;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "weeks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private Integer weekNumber; // Número de semana (1-18 para regular, 1-10 para verano)

    private String title; // Título opcional de la semana (ej: "Semana 11")

    private String description; // Descripción de los temas de la semana

    private LocalDate startDate; // Fecha de inicio de la semana

    private LocalDate endDate; // Fecha de fin de la semana

    // Relación con sesiones de la semana
    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "week")
    private List<Content> contents = new ArrayList<>();

    /**
     * Añade una sesión a la semana
     */
    public void addSession(Session session) {
        sessions.add(session);
        session.setWeek(this);
    }

    /**
     * Elimina una sesión de la semana
     */
    public void removeSession(Session session) {
        sessions.remove(session);
        session.setWeek(null);
    }
}