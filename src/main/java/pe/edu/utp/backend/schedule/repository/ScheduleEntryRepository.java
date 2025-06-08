package pe.edu.utp.backend.schedule.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.backend.schedule.model.ScheduleEntry;
import java.util.List;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {
    List<ScheduleEntry> findByStudentScheduleId(Long studentScheduleId);
    List<ScheduleEntry> findBySectionId(Long sectionId);
    List<ScheduleEntry> findBySessionId(Long sessionId);
    @Modifying
    @Transactional
    @Query("DELETE FROM ScheduleEntry e WHERE e.studentSchedule.id = :scheduleId")
    void deleteAllByScheduleId(@Param("scheduleId") Long scheduleId);
}