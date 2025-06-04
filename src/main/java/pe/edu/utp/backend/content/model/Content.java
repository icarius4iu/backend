package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    /**
     * Tipos de contenido
     */
    public enum ContentType {
        COURSE("Contenido de curso"),
        SECTION("Contenido de sección"),
        WEEK("Contenido de semana"),
        SESSION("Contenido de sesión"),
        RESOURCE("Recurso"),
        TOPIC("Tema");

        private final String displayName;

        ContentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    // Relaciones directas con entidades del módulo course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_content_id")
    private Content parentContent;

    @OneToMany(mappedBy = "parentContent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Content> subContents = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContentResource> resources = new ArrayList<>();

    /**
     * Añade un sub-contenido
     */
    public void addSubContent(Content subContent) {
        subContents.add(subContent);
        subContent.setParentContent(this);
    }

    /**
     * Elimina un sub-contenido
     */
    public void removeSubContent(Content subContent) {
        subContents.remove(subContent);
        subContent.setParentContent(null);
    }

    /**
     * Añade un recurso
     */
    public void addResource(ContentResource resource) {
        resources.add(resource);
        resource.setContent(this);
    }

    /**
     * Elimina un recurso
     */
    public void removeResource(ContentResource resource) {
        resources.remove(resource);
        resource.setContent(null);
    }

    /**
     * Actualiza la fecha de actualización antes de guardar o actualizar
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