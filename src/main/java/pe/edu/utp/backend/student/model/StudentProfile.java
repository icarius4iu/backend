package pe.edu.utp.backend.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Datos espejo de Student
    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "status")
    private String status;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "modality")
    private String modality;

    // Datos espejo de StudentInformation
    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "personal_email")
    private String personalEmail;


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

    /**
     * Sincroniza los datos del perfil con Student y StudentInformation
     * Este método debe llamarse cuando se crea o actualiza el perfil
     */
    public void syncFromStudent() {
        if (student != null) {
            this.studentCode = student.getStudentCode();
            this.fullName = student.getFullName();
            this.status = student.getStatus() != null ? student.getStatus().getDisplayName() : null;
            this.faculty = student.getFaculty();
            this.modality = student.getModality() != null ? student.getModality().getDisplayName() : null;

            if (student.getInformation() != null) {
                StudentInformation info = student.getInformation();
                this.documentType = info.getDocumentType() != null ?
                        info.getDocumentType().getDisplayName() : null;
                this.documentNumber = info.getDocumentNumber();
                this.mobilePhone = info.getMobilePhone();
                this.personalEmail = info.getPersonalEmail();
            }
        }
    }
}