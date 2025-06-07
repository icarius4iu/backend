package pe.edu.utp.backend.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import pe.edu.utp.backend.student.model.Student;

@Entity
@Table(name = "student_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Genera automáticamente el email institucional basado en el código de estudiante
    @Formula("CONCAT(student.student_code, '@utp.edu.pe')")
    @Column(name = "institutional_email", unique = true, nullable = false)
    private String institutionalEmail;

    // Obtiene el correo personal como correo de recuperación
    @Formula("(SELECT si.personal_email FROM student_information si WHERE si.student_id = student_id)")
    @Column(name = "recovery_email")
    private String recoveryEmail;

    // Si usas autenticación híbrida (Firebase + local)
    @Column(name = "password", nullable = false)
    private String password;

    // Referencia al UID de Firebase (opcional si usas Firebase Auth)
    @Column(name = "firebase_uid")
    private String firebaseUid;

    @Column(name = "status", length = 20)
    private String status; // ACTIVO, INACTIVO, BLOQUEADO, etc.

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (student != null) {
            // Genera el correo institucional basado en el código del estudiante
            this.institutionalEmail = student.getStudentCode().toLowerCase() + "@utp.edu.pe";

            // Obtiene el correo de recuperación desde la información personal
            if (student.getInformation() != null) {
                this.recoveryEmail = student.getInformation().getPersonalEmail();
            }
        }
    }

    // Constructor para facilitar la creación
    public StudentCredentials(Student student) {
        this.student = student;
        this.status = "ACTIVO";
        onSave(); // Genera los correos automáticamente
    }

    // Constructor para el flujo de registro con Firebase
    public StudentCredentials(Student student, String password, String firebaseUid) {
        this.student = student;
        this.password = password;
        this.firebaseUid = firebaseUid;
        this.status = "ACTIVO";
        onSave();
    }
}