package pe.edu.utp.backend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.content.model.ContentActivity;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.content.model.ContentResource;
import pe.edu.utp.backend.student.model.Student;

import java.util.List;

@Repository
public interface ContentActivityRepository extends JpaRepository<ContentActivity, Long> {
    List<ContentActivity> findByStudent(Student student);
    List<ContentActivity> findByContent(Content content);
    List<ContentActivity> findByResource(ContentResource resource);
    List<ContentActivity> findByStudentAndContent(Student student, Content content);
}