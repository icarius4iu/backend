package pe.edu.utp.backend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.content.model.ContentResource;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.course.model.Week;

import java.util.List;

@Repository
public interface ContentResourceRepository extends JpaRepository<ContentResource, Long> {
    List<ContentResource> findByContent(Content content);
    @Query("""
    SELECT r FROM ContentResource r
    WHERE r.content.week = :week
      AND r.downloadable = true
      AND r.resourceType = :type
    """)
    List<ContentResource> findDownloadableByWeekAndType(@Param("week") Week week, @Param("type") ContentResource.ResourceType type);
}