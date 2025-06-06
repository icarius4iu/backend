package pe.edu.utp.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {

    /**
     * Busca semanas de una sección específica
     */
    List<Week> findBySection(Section section);

    /**
     * Busca semanas de una sección ordenadas por número de semana
     */
    List<Week> findBySectionOrderByWeekNumber(Section section);

    /**
     * Busca una semana específica por su número en una sección
     */
    Optional<Week> findBySectionAndWeekNumber(Section section, Integer weekNumber);

    /**
     * Busca semanas que tienen un título específico
     */
    List<Week> findByTitleContainingIgnoreCase(String title);

    /**
     * Busca semanas que tienen fecha de inicio después de una fecha específica
     */
    List<Week> findByStartDateAfter(LocalDate date);

    /**
     * Busca semanas que tienen fecha de fin antes de una fecha específica
     */
    List<Week> findByEndDateBefore(LocalDate date);

    /**
     * Busca semanas que están activas durante una fecha específica
     */
    List<Week> findByStartDateBeforeAndEndDateAfter(LocalDate date, LocalDate sameDate);

    /**
     * Busca semanas con descripción que contenga un texto específico
     */
    List<Week> findByDescriptionContainingIgnoreCase(String description);
}