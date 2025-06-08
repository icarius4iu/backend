package pe.edu.utp.backend.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.schedule.model.StudentSchedule;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, Long> {

    Optional<StudentSchedule> findByStudent(Student student);

    Optional<StudentSchedule> findByStudentId(Long studentId);

    @Query("SELECT ss FROM StudentSchedule ss " +
            "JOIN FETCH ss.entries e " +
            "WHERE ss.student.id = :studentId " +
            "AND e.date BETWEEN :startDate AND :endDate")
    Optional<StudentSchedule> findByStudentWithEntriesInDateRange(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT ss FROM StudentSchedule ss " +
            "JOIN ss.student s " +
            "WHERE s.career.id = :careerId")
    List<StudentSchedule> findByCareer(@Param("careerId") Long careerId);

}