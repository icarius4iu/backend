package pe.edu.utp.backend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.backend.auth.model.StudentCredentials;
import pe.edu.utp.backend.student.model.Student;

import java.util.Optional;

public interface StudentCredentialsRepository extends JpaRepository<StudentCredentials, Long> {

    /**
     * Verifica si existe un estudiante con el correo institucional dado
     */
    boolean existsByInstitutionalEmail(String institutionalEmail);

    /**
     * Busca credenciales por correo institucional
     */
    Optional<StudentCredentials> findByInstitutionalEmail(String institutionalEmail);

    /**
     * Busca credenciales por el código de estudiante
     */
    @Query("SELECT sc FROM StudentCredentials sc WHERE sc.student.studentCode = :studentCode")
    Optional<StudentCredentials> findByStudentCode(@Param("studentCode") String studentCode);

    /**
     * Busca credenciales por el estudiante
     */
    Optional<StudentCredentials> findByStudent(Student student);

    /**
     * Busca credenciales por el UID de Firebase
     */
    Optional<StudentCredentials> findByFirebaseUid(String firebaseUid);

    /**
     * Actualiza el estado de las credenciales
     */
    @Query("UPDATE StudentCredentials sc SET sc.status = :status WHERE sc.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * Actualiza el UID de Firebase
     */
    @Query("UPDATE StudentCredentials sc SET sc.firebaseUid = :firebaseUid WHERE sc.id = :id")
    int updateFirebaseUid(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

    /**
     * Verifica si existe un estudiante con el UID de Firebase dado
     */
    boolean existsByFirebaseUid(String firebaseUid);

    /**
     * Cuenta el número de estudiantes activos
     */
    @Query("SELECT COUNT(sc) FROM StudentCredentials sc WHERE sc.status = 'ACTIVO'")
    long countActiveCredentials();
}