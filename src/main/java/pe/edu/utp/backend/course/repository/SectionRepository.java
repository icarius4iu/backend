package pe.edu.utp.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.student.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    /**
     * Busca secciones por su código
     */
    List<Section> findByCode(String code);

    /**
     * Busca una sección específica por curso y código
     */
    Optional<Section> findByCourseAndCode(Course course, String code);

    /**
     * Busca todas las secciones de un curso específico
     */
    List<Section> findByCourse(Course course);

    /**
     * Busca secciones por curso ordenadas por código
     */
    List<Section> findByCourseOrderByCode(Course course);

    /**
     * Busca secciones impartidas por un profesor específico
     */
    List<Section> findByProfessorsContaining(Professor professor);

    /**
     * Busca secciones en las que está inscrito un estudiante específico
     */
    List<Section> findByStudentsContaining(Student student);

    /**
     * Busca secciones con capacidad máxima específica
     */
    List<Section> findByMaxStudents(Integer maxStudents);

    /**
     * Busca secciones con URL de reunión virtual
     */
    List<Section> findByVirtualMeetingUrlIsNotNull();

    /**
     * Consulta personalizada para encontrar secciones con vacantes disponibles
     */
    @Query("SELECT s FROM Section s WHERE s.maxStudents > SIZE(s.students)")
    List<Section> findSectionsWithAvailableSeats();

    /**
     * Consulta para encontrar secciones con un porcentaje de ocupación específico
     */
    @Query("SELECT s FROM Section s WHERE SIZE(s.students) * 100 / s.maxStudents >= :percentage")
    List<Section> findSectionsWithOccupancyPercentageGreaterThan(@Param("percentage") int percentage);

    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.weeks w " +
            "LEFT JOIN FETCH w.sessions ses " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH s.cicle " +
            "WHERE s.id IN (SELECT ss.id FROM Student st JOIN st.sections ss WHERE st.id = :studentId)")
    List<Section> findSectionsWithWeeksAndSessionsByStudentId(@Param("studentId") Long studentId);

    Optional<Section> findByCourseAndCode(Course course, String sectionCode);
}