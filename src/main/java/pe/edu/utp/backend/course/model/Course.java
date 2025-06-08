package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.util.cicle.model.Cicle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"careers", "sections", "contents"})
// CORRECTO: usar SOLO los campos identificadores
@EqualsAndHashCode(of = {"id", "code"})
public class Course {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private Integer weeklyHours;

    @ManyToOne
    @JoinColumn(name = "cicle_id")
    private Cicle cicle;

    // Relación muchos a muchos con Career (Carreras)
    @ManyToMany
    @JoinTable(
            name = "course_career",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "career_id")
    )
    @Builder.Default
    private Set<Career> careers = new HashSet<>();

    // Relación con secciones (opcional, según tu implementación anterior)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Section> sections = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private List<Content> contents = new ArrayList<>();



    /**
     * Añade una carrera a este curso
     */
    public void addCareer(Career career) {
        careers.add(career);
        career.getCourses().add(this);
    }

    /**
     * Elimina una carrera de este curso
     */
    public void removeCareer(Career career) {
        careers.remove(career);
        career.getCourses().remove(this);
    }

    /**
     * Añade una sección al curso
     */
    public void addSection(Section section) {
        sections.add(section);
        section.setCourse(this);
    }

    /**
     * Elimina una sección del curso
     */
    public void removeSection(Section section) {
        sections.remove(section);
        section.setCourse(null);
    }
}