package pe.edu.utp.backend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.backend.content.model.Assignment;


import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Optional<Assignment> findByContentId(Long contentId);
    // Otros métodos de consulta si los necesitas
}