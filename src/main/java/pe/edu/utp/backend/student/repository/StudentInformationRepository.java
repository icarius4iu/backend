package pe.edu.utp.backend.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.model.StudentInformation.DocumentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentInformationRepository extends JpaRepository<StudentInformation, Long> {

    /**
     * Busca información de estudiante por número de documento
     */
    Optional<StudentInformation> findByDocumentNumber(String documentNumber);

    /**
     * Busca información de estudiante por tipo y número de documento
     */
    Optional<StudentInformation> findByDocumentTypeAndDocumentNumber(
            DocumentType documentType, String documentNumber);

    /**
     * Busca información de estudiante por correo personal
     */
    Optional<StudentInformation> findByPersonalEmail(String email);

    /**
     * Busca información de estudiante por número telefónico
     */
    Optional<StudentInformation> findByMobilePhone(String mobilePhone);

    /**
     * Busca información de estudiantes por apellido paterno
     */
    List<StudentInformation> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Busca información de estudiantes por nombre completo
     */
    @Query("SELECT si FROM StudentInformation si WHERE " +
            "CONCAT(si.firstName, ' ', si.lastName, ' ', COALESCE(si.motherLastName, '')) " +
            "LIKE %:fullName%")
    List<StudentInformation> findByFullNameContaining(@Param("fullName") String fullName);

    /**
     * Busca información de estudiantes por departamento
     */
    List<StudentInformation> findByDepartment(String department);

    /**
     * Cuenta estudiantes por tipo de documento
     */
    @Query("SELECT si.documentType, COUNT(si) FROM StudentInformation si " +
            "GROUP BY si.documentType")
    List<Object[]> countByDocumentType();
}