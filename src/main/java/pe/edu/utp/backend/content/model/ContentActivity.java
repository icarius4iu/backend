package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentActivity {

    /**
     * Tipos de actividades con el contenido
     */
    public enum ActivityType {
        VIEW("Visualización"),
        DOWNLOAD("Descarga"),
        COMPLETE("Completado"),
        SUBMIT("Envío"),
        FEEDBACK("Comentario");

        private final String displayName;

        ActivityType(String displayName) {
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
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private ContentResource resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(length = 1000)
    private String comment;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * Establece la fecha de actividad automáticamente
     */
    @PrePersist
    protected void onCreate() {
        activityDate = LocalDateTime.now();
    }
}