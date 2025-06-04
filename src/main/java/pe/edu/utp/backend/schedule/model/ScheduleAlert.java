package pe.edu.utp.backend.schedule.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAlert {

    /**
     * Tipos de alertas
     */
    public enum AlertType {
        UPCOMING_EXAM("Examen próximo"),
        UPCOMING_CLASS("Clase próxima"),
        DEADLINE("Fecha límite"),
        SCHEDULE_CHANGE("Cambio en el horario"),
        CANCELATION("Cancelación de sesión");

        private final String displayName;

        AlertType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Niveles de prioridad
     */
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_entry_id")
    private ScheduleEntry scheduleEntry;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "alert_for", nullable = false)
    private LocalDateTime alertFor;

    @Column(name = "is_read")
    private Boolean read;

    @Column(name = "is_dismissed")
    private Boolean dismissed;

    /**
     * Marca la alerta como leída
     */
    public void markAsRead() {
        this.read = true;
    }

    /**
     * Desestima la alerta
     */
    public void dismiss() {
        this.dismissed = true;
    }
}