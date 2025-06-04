package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResource {

    /**
     * Tipos de recursos
     */
    public enum ResourceType {
        PDF("PDF"),
        VIDEO("Video"),
        PRESENTATION("Presentación"),
        LINK("Enlace"),
        CODE("Código"),
        QUIZ("Cuestionario"),
        ASSIGNMENT("Tarea"),
        IMAGE("Imagen"),
        DOCUMENT("Documento"),
        AUDIO("Audio"),
        OTHER("Otro");

        private final String displayName;

        ResourceType(String displayName) {
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
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "embed_code", length = 1000)
    private String embedCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_downloadable")
    private Boolean downloadable;

    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * Actualiza las fechas de creación/actualización
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}