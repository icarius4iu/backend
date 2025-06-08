package pe.edu.utp.backend.content.model;


import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.content.model.Content;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"content", "submissions"})
@EqualsAndHashCode(of = {"id"})
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1:1 con el contenido (Content)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private Content content;

    // Fecha límite de entrega
    private LocalDateTime dueDate;

    // Otros campos posibles
    private Double maxGrade;
    private String instructions;

    // Entregas de estudiantes
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AssignmentSubmission> submissions = new HashSet<>();
}