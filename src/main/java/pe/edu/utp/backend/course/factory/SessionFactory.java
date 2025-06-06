package pe.edu.utp.backend.course.factory;

import org.springframework.stereotype.Component;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class SessionFactory {

    /**
     * Crea una sesión de teoría
     */
    public Session createTheorySession(Week week, LocalDate sessionDate,
                                       LocalTime startTime, LocalTime endTime) {

        return Session.builder()
                .week(week)
                .title("Clase Teórica - Semana " + week.getWeekNumber())
                .type(Session.SessionType.TEORIA)
                .sessionDate(sessionDate)
                .dayOfWeek(sessionDate.getDayOfWeek())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    /**
     * Crea una sesión de práctica
     */
    public Session createPracticeSession(Week week, LocalDate sessionDate,
                                         LocalTime startTime, LocalTime endTime) {

        return Session.builder()
                .week(week)
                .title("Clase Práctica - Semana " + week.getWeekNumber())
                .type(Session.SessionType.PRACTICA)
                .sessionDate(sessionDate)
                .dayOfWeek(sessionDate.getDayOfWeek())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    /**
     * Crea una sesión de laboratorio
     */
    public Session createLabSession(Week week, LocalDate sessionDate,
                                    LocalTime startTime, LocalTime endTime, String buildingName, String roomNumber) {

        return Session.builder()
                .week(week)
                .title("Laboratorio - Semana " + week.getWeekNumber())
                .type(Session.SessionType.LABORATORIO)
                .sessionDate(sessionDate)
                .dayOfWeek(sessionDate.getDayOfWeek())
                .startTime(startTime)
                .endTime(endTime)
                .buildingName(buildingName)
                .roomNumber(roomNumber)
                .build();
    }

    /**
     * Crea una sesión de evaluación
     */
    public Session createEvaluationSession(Week week, LocalDate sessionDate,
                                           String title, LocalTime startTime, LocalTime endTime) {

        return Session.builder()
                .week(week)
                .title(title)
                .type(Session.SessionType.EVALUACION)
                .description("Evaluación para la semana " + week.getWeekNumber())
                .sessionDate(sessionDate)
                .dayOfWeek(sessionDate.getDayOfWeek())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    /**
     * Crea una sesión virtual
     */
    public Session createVirtualSession(Week week, LocalDate sessionDate,
                                        LocalTime startTime, LocalTime endTime, String meetingUrl) {

        Session session = Session.builder()
                .week(week)
                .title("Clase Virtual - Semana " + week.getWeekNumber())
                .type(Session.SessionType.TEORIA)
                .sessionDate(sessionDate)
                .dayOfWeek(sessionDate.getDayOfWeek())
                .startTime(startTime)
                .endTime(endTime)
                .meetingUrl(meetingUrl)
                .build();

        return session;
    }
}