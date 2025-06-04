package pe.edu.utp.backend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "is_visible")
    private Boolean visible;

    @Column(name = "is_sequential")
    private Boolean sequential;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(name = "required_view_percentage")
    private Integer requiredViewPercentage;

    @Column(name = "auto_release")
    private Boolean autoRelease;

    @Column(name = "password_protected")
    private Boolean passwordProtected;

    @Column(name = "access_password")
    private String accessPassword;

    @Column(name = "allow_download")
    private Boolean allowDownload;

    @Column(name = "track_progress")
    private Boolean trackProgress;

    /**
     * Verifica si el contenido está disponible en base a las fechas
     */
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();

        if (!visible) {
            return false;
        }

        if (releaseDate != null && now.isBefore(releaseDate)) {
            return false;
        }

        if (expireDate != null && now.isAfter(expireDate)) {
            return false;
        }

        return true;
    }
}