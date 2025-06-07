package pe.edu.utp.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Course.CourseType;
import pe.edu.utp.backend.util.cicle.model.Cicle;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Busca un curso por su código único
     */
    Optional<Course> findByCode(String code);

    /**
     * Verifica si existe un curso con el código proporcionado
     */
    boolean existsByCode(String code);

    /**
     * Busca cursos por nombre (coincidencia exacta)
     */
    List<Course> findByName(String name);

    /**
     * Busca cursos que contengan el texto en el nombre (case insensitive)
     */
    List<Course> findByNameContainingIgnoreCase(String name);

    /**
     * Busca cursos por tipo (PRESENCIAL, VIRTUAL_VIVO, VIRTUAL_24_7)
     */
    List<Course> findByType(CourseType type);

    /**
     * Busca cursos por número de créditos
     */
    List<Course> findByCredits(Integer credits);

    /**
     * Busca cursos por horas semanales
     */
    List<Course> findByWeeklyHours(Integer weeklyHours);

    /**
     * Busca cursos de un ciclo específico
     */
    List<Course> findByCicle(Cicle cicle);

    /**
     * Busca cursos asociados a una carrera específica
     */
    List<Course> findByCareersContaining(Career career);

    /**
     * Busca cursos por ciclo y carrera
     */
    List<Course> findByCicleAndCareersContaining(Cicle cicle, Career career);

    /**
     * Busca cursos con descripción que contenga un texto específico
     */
    List<Course> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Consulta personalizada para encontrar cursos con secciones disponibles (no llenas)
     */
    @Query("SELECT DISTINCT c FROM Course c JOIN c.sections s WHERE s.maxStudents > SIZE(s.students)")
    List<Course> findCoursesWithAvailableSections();
}