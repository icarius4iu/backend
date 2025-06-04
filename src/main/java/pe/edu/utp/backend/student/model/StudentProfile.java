package pe.edu.utp.backend.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.util.Cicle;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Foto del estudiante
    @Column(name = "photo_url")
    private String photoUrl;

    // Número de WhatsApp para contacto
    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    // Nombre completo (para mostrar)
    @Column(name = "display_name")
    private String displayName;

    // DNI (documento de identidad)
    @Column(name = "dni")
    private String dni;

    // Ciclo actual del estudiante
    @ManyToOne
    @JoinColumn(name = "current_cicle_id")
    private Cicle currentCicle;

    // Fecha de última actualización del perfil
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /**
     * Actualiza la fecha de última actualización
     */
    @PreUpdate
    @PrePersist
    public void updateLastUpdated() {
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Obtiene la URL de la foto o una foto por defecto
     */
    public String getProfilePhotoOrDefault() {
        return photoUrl != null && !photoUrl.isEmpty()
                ? photoUrl
                : "/assets/images/default-profile.png";
    }
}