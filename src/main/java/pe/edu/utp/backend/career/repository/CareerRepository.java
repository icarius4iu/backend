package pe.edu.utp.backend.career.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.career.model.Career;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

    /**
     * Busca una carrera por su código
     */
    Optional<Career> findByCode(String code);

    /**
     * Busca carreras por nombre (búsqueda parcial)
     */
    List<Career> findByNameContainingIgnoreCase(String name);

    /**
     * Busca carreras por duración en semestres
     */
    List<Career> findByDurationSemesters(Integer durationSemesters);

    /**
     * Verifica si existe una carrera con el código proporcionado
     */
    boolean existsByCode(String code);
}