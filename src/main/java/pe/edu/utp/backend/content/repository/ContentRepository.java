package pe.edu.utp.backend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.content.model.ContentActivity;
import pe.edu.utp.backend.content.model.ContentResource;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.model.Session;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCourse(Course course);
    List<Content> findBySection(Section section);
    List<Content> findByWeek(Week week);
    List<Content> findBySession(Session session);

    // Buscar contenidos publicados y visibles por curso/sección/semana/sesión
    List<Content> findByCourseAndPublishedTrue(Course course);
    List<Content> findBySectionAndPublishedTrue(Section section);
    List<Content> findByWeekAndPublishedTrue(Week week);
    List<Content> findBySessionAndPublishedTrue(Session session);

    @Query("""
    SELECT c FROM Content c
    JOIN ContentConfig cfg ON cfg.content = c
    WHERE c.section = :section
    AND c.published = true
    AND (cfg.visible = true OR cfg.visible IS NULL)
    AND (cfg.releaseDate IS NULL OR cfg.releaseDate <= :now)
    AND (cfg.expireDate IS NULL OR cfg.expireDate >= :now)
    """)
    List<Content> findVisiblePublishedBySection(@Param("section") Section section, @Param("now") LocalDateTime now);

    @Query("""
    SELECT c FROM Content c
    WHERE (c.session = :session OR c.week = :week OR c.section = :section OR c.course = :course)
      AND c.published = true
    """)
    List<Content> findAllRelevantForScheduleEntry(@Param("session") Session session,
                                                  @Param("week") Week week,
                                                  @Param("section") Section section,
                                                  @Param("course") Course course);



    @Query("""
    SELECT a FROM ContentActivity a
    WHERE a.student.id = :studentId
      AND a.activityType = :activityType
      AND a.activityDate BETWEEN :from AND :to
    """)
    List<ContentActivity> findByStudentAndTypeAndDateRange(@Param("studentId") Long studentId,
                                                           @Param("activityType") ContentActivity.ActivityType activityType,
                                                           @Param("from") LocalDateTime from,
                                                           @Param("to") LocalDateTime to);
    @Query("""
    SELECT c FROM Content c
    WHERE c.course = :course
      AND LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
      AND c.published = true
    """)
    List<Content> searchByTitleAndCourse(@Param("course") Course course, @Param("searchTerm") String searchTerm);



}