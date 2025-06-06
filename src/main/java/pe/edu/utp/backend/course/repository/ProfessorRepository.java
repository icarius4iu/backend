package pe.edu.utp.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    /**
     * Busca profesor por correo electrónico
     */
    Optional<Professor> findByEmail(String email);

    /**
     * Busca profesor por código
     */
    Optional<Professor> findByProfessorCode(String professorCode);

    /**
     * Verifica si existe un profesor con el código proporcionado
     */
    boolean existsByProfessorCode(String professorCode);

    /**
     * Busca profesores por nombre y apellido
     */
    List<Professor> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Busca profesores por nombre (coincidencia parcial, case insensitive)
     */
    List<Professor> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Busca profesores por apellido (coincidencia parcial, case insensitive)
     */
    List<Professor> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Busca profesores por departamento
     */
    List<Professor> findByDepartment(String department);

    /**
     * Busca profesores por especialización
     */
    List<Professor> findBySpecialization(String specialization);

    /**
     * Busca profesores que imparten una sección específica
     */
    List<Professor> findBySectionsContaining(Section section);

    /**
     * Consulta personalizada para encontrar profesores que imparten un curso específico
     */
    @Query("SELECT DISTINCT p FROM Professor p JOIN p.sections s WHERE s.course = :course")
    List<Professor> findProfessorsByCourse(@Param("course") Course course);

    /**
     * Consulta personalizada para encontrar profesores con su carga horaria
     */
    @Query("SELECT p.id, p.firstName, p.lastName, COUNT(s) as sectionCount " +
            "FROM Professor p LEFT JOIN p.sections s GROUP BY p.id, p.firstName, p.lastName")
    List<Object[]> findProfessorsWithSectionCount();
}