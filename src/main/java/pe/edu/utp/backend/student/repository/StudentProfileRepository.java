package pe.edu.utp.backend.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.student.model.StudentProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    /**
     * Busca perfil por ID de estudiante
     */
    Optional<StudentProfile> findByStudentId(Long studentId);

    /**
     * Busca perfil por código de estudiante
     */
    Optional<StudentProfile> findByStudentCode(String studentCode);

    /**
     * Busca perfiles por número de documento
     */
    Optional<StudentProfile> findByDocumentNumber(String documentNumber);

    /**
     * Busca perfiles por nombre completo
     */
    List<StudentProfile> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Busca perfiles por facultad
     */
    List<StudentProfile> findByFaculty(String faculty);

    /**
     * Busca perfiles actualizados después de cierta fecha
     */
    List<StudentProfile> findByLastUpdatedAfter(LocalDateTime date);

    /**
     * Busca perfiles que contengan URL de foto (no nulos y no vacíos)
     */
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.photoUrl IS NOT NULL AND sp.photoUrl <> ''")
    List<StudentProfile> findAllWithPhotos();

    /**
     * Busca perfiles por estado del estudiante
     */
    List<StudentProfile> findByStatus(String status);

    /**
     * Busca perfiles por modalidad
     */
    List<StudentProfile> findByModality(String modality);

    /**
     * Busca perfiles por email personal
     */
    Optional<StudentProfile> findByPersonalEmail(String email);

    /**
     * Busca perfiles por número de teléfono móvil
     */
    Optional<StudentProfile> findByMobilePhone(String mobilePhone);

    /**
     * Cuenta perfiles por facultad
     */
    @Query("SELECT sp.faculty, COUNT(sp) FROM StudentProfile sp GROUP BY sp.faculty")
    List<Object[]> countByFaculty();
}