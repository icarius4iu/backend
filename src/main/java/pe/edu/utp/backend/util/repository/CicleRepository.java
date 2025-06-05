package pe.edu.utp.backend.util.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.util.Cicle;
import pe.edu.utp.backend.util.Cicle.CicleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CicleRepository extends JpaRepository<Cicle, Long> {

    /**
     * Busca ciclo por nombre
     */
    Optional<Cicle> findByName(String name);

    /**
     * Busca ciclos por tipo
     */
    List<Cicle> findByType(CicleType type);

    /**
     * Busca ciclo más reciente por tipo (debería ser único)
     */
    Optional<Cicle> findFirstByTypeOrderByStartDateDesc(CicleType type);

    /**
     * Busca ciclos por fecha de inicio
     */
    List<Cicle> findByStartDateAfter(LocalDate date);

    /**
     * Busca ciclos por fecha de fin
     */
    List<Cicle> findByEndDateBefore(LocalDate date);

    /**
     * Busca ciclos que están en curso en una fecha determinada
     */
    List<Cicle> findByStartDateBeforeAndEndDateAfter(LocalDate date, LocalDate sameDate);
}