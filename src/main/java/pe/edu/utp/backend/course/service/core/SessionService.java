package pe.edu.utp.backend.course.service.core;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.repository.SessionRepository;
import pe.edu.utp.backend.course.repository.WeekRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SessionService extends BaseAcademicService<Session, Long> {

    private final WeekRepository weekRepository;

    public SessionService(SessionRepository sessionRepository, WeekRepository weekRepository) {
        super(sessionRepository);
        this.weekRepository = weekRepository;
    }

    public SessionRepository getSessionRepository() {
        return (SessionRepository) repository;
    }

    @Transactional(readOnly = true)
    public List<Session> findByWeek(Week week) {
        return getSessionRepository().findByWeekOrderBySessionDateAscStartTimeAsc(week);
    }

    @Transactional(readOnly = true)
    public List<Session> findByType(Session.SessionType type) {
        return getSessionRepository().findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Session> findByDate(LocalDate date) {
        return getSessionRepository().findBySessionDate(date);
    }

    @Transactional(readOnly = true)
    public List<Session> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return getSessionRepository().findBySessionDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Session> findByDayOfWeek(DayOfWeek dayOfWeek) {
        return getSessionRepository().findByDayOfWeek(dayOfWeek);
    }

    @Transactional
    public Session createSession(Session session) {
        validateSession(session);
        return save(session);
    }

    @Transactional
    public Session updateSessionSchedule(Long sessionId, LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
        Session session = getById(sessionId);

        if (startTime.isAfter(endTime)) {
            throw new InvalidAcademicEntityException("La hora de inicio debe ser anterior a la hora de fin");
        }

        session.setSessionDate(sessionDate);
        session.setDayOfWeek(sessionDate.getDayOfWeek());
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        return save(session);
    }

    @Transactional
    public Session updateVirtualMeetingDetails(Long sessionId, String meetingUrl, String meetingId, String meetingPassword) {
        Session session = getById(sessionId);

        // Validar que la sesión sea virtual
        if (!session.isVirtualVivo() && !session.isVirtual247()) {
            throw new InvalidAcademicEntityException("Solo se pueden actualizar detalles de reunión virtual para sesiones virtuales");
        }

        session.setMeetingUrl(meetingUrl);
        session.setMeetingId(meetingId);
        session.setMeetingPassword(meetingPassword);

        return save(session);
    }

    @Transactional
    public Session updateClassroomDetails(Long sessionId, String buildingName, String roomNumber) {
        Session session = getById(sessionId);

        // Validar que la sesión sea presencial
        if (!session.isPresencial()) {
            throw new InvalidAcademicEntityException("Solo se pueden actualizar detalles de aula para sesiones presenciales");
        }

        session.setBuildingName(buildingName);
        session.setRoomNumber(roomNumber);

        return save(session);
    }

    @Transactional(readOnly = true)
    public boolean checkScheduleConflict(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Session> overlappingSessions = getSessionRepository().findSessionsOverlappingWithTimeRange(
                date, startTime, endTime);
        return !overlappingSessions.isEmpty();
    }

    /**
     * Valida que una sesión sea válida
     */
    private void validateSession(Session session) {
        if (session == null) {
            throw new InvalidAcademicEntityException("La sesión no puede ser nula");
        }

        if (session.getWeek() == null) {
            throw new InvalidAcademicEntityException("La sesión debe estar asociada a una semana");
        }

        if (session.getSessionDate() == null) {
            throw new InvalidAcademicEntityException("La fecha de la sesión es obligatoria");
        }

        if (session.getType() == null) {
            throw new InvalidAcademicEntityException("El tipo de sesión es obligatorio");
        }

        if (session.getStartTime() != null && session.getEndTime() != null) {
            if (session.getStartTime().isAfter(session.getEndTime())) {
                throw new InvalidAcademicEntityException("La hora de inicio debe ser anterior a la hora de fin");
            }
        }

        // Validar que la fecha de la sesión esté dentro del rango de la semana si tiene fechas definidas
        Week week = session.getWeek();
        if (week.getStartDate() != null && week.getEndDate() != null) {
            if (session.getSessionDate().isBefore(week.getStartDate()) ||
                    session.getSessionDate().isAfter(week.getEndDate())) {
                throw new InvalidAcademicEntityException(
                        "La fecha de la sesión debe estar dentro del rango de fechas de la semana");
            }
        }
    }
}