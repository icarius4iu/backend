package pe.edu.utp.backend.util.career.model;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.course.model.Course;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "careers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"courses"}) // Excluir colecciones
@EqualsAndHashCode(of = {"id", "code"}) // Solo usar campos identificadores
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    // Facultad a la que pertenece la carrera
    private String faculty;

    // Duración en semestres
    private Integer durationSemesters;

    // Relación muchos a muchos con Course
    @ManyToMany(mappedBy = "careers")
    @Builder.Default
    private Set<Course> courses = new HashSet<>();
}