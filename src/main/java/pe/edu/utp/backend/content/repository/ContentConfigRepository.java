package pe.edu.utp.backend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.content.model.ContentConfig;

import java.util.Optional;

@Repository
public interface ContentConfigRepository extends JpaRepository<ContentConfig, Long> {
    Optional<ContentConfig> findByContent(Content content);
}