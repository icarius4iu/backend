package pe.edu.utp.backend.content.model;


import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"assignment", "student"})
@EqualsAndHashCode(of = {"id"})
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: a qué Assignment corresponde la entrega
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    // Relación: quién entregó
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Datos del archivo entregado
    private String fileUrl;
    private String fileName;
    private Long fileSize;

    private LocalDateTime submittedAt;

    // (Opcional) Nota y feedback
    private Double grade;
    private String feedback;
}