package pe.edu.utp.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Session.SessionType;
import pe.edu.utp.backend.course.model.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Busca sesiones de una semana específica
     */
    List<Session> findByWeek(Week week);

    /**
     * Busca sesiones de una semana ordenadas por fecha y hora de inicio
     */
    List<Session> findByWeekOrderBySessionDateAscStartTimeAsc(Week week);

    /**
     * Busca sesiones por tipo (teoría, práctica, etc.)
     */
    List<Session> findByType(SessionType type);

    /**
     * Busca sesiones por fecha específica
     */
    List<Session> findBySessionDate(LocalDate date);

    /**
     * Busca sesiones por día de la semana
     */
    List<Session> findByDayOfWeek(DayOfWeek dayOfWeek);

    /**
     * Busca sesiones en un rango de fechas
     */
    List<Session> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Busca sesiones que tienen título que contiene un texto específico
     */
    List<Session> findByTitleContainingIgnoreCase(String title);

    /**
     * Busca sesiones con hora de inicio después de una hora específica
     */
    List<Session> findByStartTimeAfter(LocalTime time);

    /**
     * Busca sesiones con hora de fin antes de una hora específica
     */
    List<Session> findByEndTimeBefore(LocalTime time);

    /**
     * Busca sesiones en un edificio específico (para sesiones presenciales)
     */
    List<Session> findByBuildingName(String buildingName);

    /**
     * Busca sesiones en un aula específica
     */
    List<Session> findByRoomNumber(String roomNumber);

    /**
     * Busca sesiones virtuales (con URL de reunión)
     */
    List<Session> findByMeetingUrlIsNotNull();

    /**
     * Consulta personalizada para encontrar sesiones programadas para un rango horario específico
     */
    @Query("SELECT s FROM Session s WHERE s.sessionDate = :date AND " +
            "((s.startTime <= :endTime AND s.endTime >= :startTime))")
    List<Session> findSessionsOverlappingWithTimeRange(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}