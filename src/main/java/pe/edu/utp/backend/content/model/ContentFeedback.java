package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_feedbacks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "rating")
    private Integer rating; // 1-5 estrellas

    @Column(length = 1000)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_helpful")
    private Boolean helpful;

    @Column(name = "report_issue")
    private String reportIssue;

    @Column(name = "is_reviewed")
    private Boolean reviewed;

    @Column(name = "review_comment")
    private String reviewComment;

    /**
     * Establece la fecha de creación automáticamente
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}