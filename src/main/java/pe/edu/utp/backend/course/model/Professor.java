package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String specialization;

    private String department;

    // Código o ID del profesor en el sistema universitario
    @Column(nullable = false, unique = true)
    private String professorCode;

    // Relación muchos a muchos con Section
    @ManyToMany(mappedBy = "professors")
    @Builder.Default
    private Set<Section> sections = new HashSet<>();

    /**
     * Obtiene el nombre completo del profesor
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}