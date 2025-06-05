package pe.edu.utp.backend.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.career.model.Career;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.Student.Status;
import pe.edu.utp.backend.student.model.Student.Modality;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Busca estudiante por código
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Busca estudiantes por estado
     */
    List<Student> findByStatus(Status status);

    /**
     * Busca estudiantes por carrera
     */
    List<Student> findByCareer(Career career);

    /**
     * Busca estudiantes por carrera y estado
     */
    List<Student> findByCareerAndStatus(Career career, Status status);

    /**
     * Busca estudiantes por facultad
     */
    List<Student> findByFaculty(String faculty);

    /**
     * Busca estudiantes por modalidad
     */
    List<Student> findByModality(Modality modality);

    /**
     * Busca estudiantes por campus
     */
    List<Student> findByCampus(String campus);

    /**
     * Busca estudiantes inscritos después de una fecha
     */
    List<Student> findByEnrollmentDateAfter(LocalDate date);

    /**
     * Busca estudiantes por documentos de identidad
     */
    @Query("SELECT s FROM Student s JOIN s.information i " +
            "WHERE i.documentNumber = :documentNumber")
    Optional<Student> findByDocumentNumber(@Param("documentNumber") String documentNumber);

    /**
     * Busca estudiantes por nombre completo
     */
    @Query("SELECT s FROM Student s JOIN s.information i " +
            "WHERE CONCAT(i.firstName, ' ', i.lastName, ' ', COALESCE(i.motherLastName, '')) " +
            "LIKE %:fullName%")
    List<Student> findByFullNameContaining(@Param("fullName") String fullName);

    /**
     * Cuenta estudiantes por carrera
     */
    @Query("SELECT s.career.name, COUNT(s) FROM Student s " +
            "GROUP BY s.career.name")
    List<Object[]> countByCareerName();

    /**
     * Cuenta estudiantes por estado
     */
    @Query("SELECT s.status, COUNT(s) FROM Student s GROUP BY s.status")
    List<Object[]> countByStatus();

    /**
     * Cuenta estudiantes por modalidad
     */
    @Query("SELECT s.modality, COUNT(s) FROM Student s GROUP BY s.modality")
    List<Object[]> countByModality();
}