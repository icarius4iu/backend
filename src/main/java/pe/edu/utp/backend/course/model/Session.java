package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.content.model.Content;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    /**
     * Tipo de sesión
     */
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
    private String title; // Título de la sesión

    @Column(length = 2000)
    private String description; // Descripción o contenido de la sesión

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type; // Tipo de sesión

    @Column(nullable = false)
    private LocalDate sessionDate; // Fecha específica de la sesión

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // Día de la semana

    private LocalTime startTime; // Hora de inicio

    private LocalTime endTime; // Hora de fin

    // Atributos para sesiones presenciales
    private String buildingName; // Nombre del edificio

    private String roomNumber; // Número de aula/laboratorio

    // Atributos para sesiones virtuales
    private String meetingUrl; // URL de la reunión virtual

    private String meetingId; // ID de la reunión

    private String meetingPassword; // Contraseña de la reunión

    @OneToMany(mappedBy = "session")
    private List<Content> contents = new ArrayList<>();


    /**
     * Determina si la sesión es presencial
     */
    public boolean isPresencial() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getCourse() != null &&
                week.getSection().getCourse().getType() == Course.CourseType.PRESENCIAL;
    }

    /**
     * Determina si la sesión es virtual en vivo
     */
    public boolean isVirtualVivo() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getCourse() != null &&
                week.getSection().getCourse().getType() == Course.CourseType.VIRTUAL_VIVO;
    }

    /**
     * Determina si la sesión es virtual 24/7
     */
    public boolean isVirtual247() {
        return week != null &&
                week.getSection() != null &&
                week.getSection().getCourse() != null &&
                week.getSection().getCourse().getType() == Course.CourseType.VIRTUAL_24_7;
    }
}