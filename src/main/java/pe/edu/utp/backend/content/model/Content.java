package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;  // Nivel al que pertenece (COURSE, SECTION, WEEK, SESSION)

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;  // Tipo de recurso (SYLLABUS, PRESENTATION, EXERCISE, etc)

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;  // Tipo de archivo (PDF, WORD, VIDEO, LINK, etc)

    // Relaciones con diferentes niveles (solo uno será no nulo según contentType)
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "week_id")
    private Week week;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    // Metadatos de archivo y acceso
    private String fileUrl;
    private Long fileSize;
    private String fileName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;  // Para rastrear qué profesor subió el contenido

    @Column(name = "is_published")
    private boolean published;  // Para controlar visibilidad

    // Enums encapsulados
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

    public enum ResourceType {
        SYLLABUS("Sílabo"),
        PRESENTATION("Presentación"),
        EXERCISE("Ejercicio"),
        ASSIGNMENT("Tarea"),
        EXAM("Examen"),
        READING("Lectura"),
        REFERENCE("Material de referencia"),
        SOLUTION("Solución"),
        RUBRIC("Rúbrica"),
        OTHER("Otro");

        private final String displayName;

        ResourceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum FileType {
        PDF("PDF"),
        WORD("Word"),
        EXCEL("Excel"),
        POWERPOINT("PowerPoint"),
        IMAGE("Imagen"),
        VIDEO("Video"),
        AUDIO("Audio"),
        LINK("Enlace"),
        CODE("Código"),
        ZIP("Archivo comprimido"),
        OTHER("Otro");

        private final String displayName;

        FileType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}