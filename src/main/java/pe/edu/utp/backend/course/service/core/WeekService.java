package pe.edu.utp.backend.course.service.core;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.course.repository.WeekRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WeekService extends BaseAcademicService<Week, Long> {

    private final SectionRepository sectionRepository;
    private final SessionService sessionService;

    @Autowired
    private EntityManager entityManager; // AÑADIDO: Inyectar EntityManager

    public WeekService(WeekRepository weekRepository, SectionRepository sectionRepository, SessionService sessionService) {
        super(weekRepository);
        this.sectionRepository = sectionRepository;
        this.sessionService = sessionService;
    }

    public WeekRepository getWeekRepository() {
        return (WeekRepository) repository;
    }

    @Transactional(readOnly = true)
    public List<Week> findWeeksBySection(Section section) {
        return getWeekRepository().findBySectionOrderByWeekNumber(section);
    }

    @Transactional(readOnly = true)
    public Optional<Week> findBySectionAndWeekNumber(Section section, Integer weekNumber) {
        return getWeekRepository().findBySectionAndWeekNumber(section, weekNumber);
    }

    @Transactional(readOnly = true)
    public List<Week> findActiveWeeks(LocalDate date) {
        return getWeekRepository().findByStartDateBeforeAndEndDateAfter(date, date);
    }

    @Transactional
    public Week createWeek(Week week) {
        validateWeek(week);
        return save(week);
    }

    @Transactional
    public Week updateWeekDates(Long weekId, LocalDate startDate, LocalDate endDate) {
        Week week = getById(weekId);

        if (startDate.isAfter(endDate)) {
            throw new InvalidAcademicEntityException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        week.setStartDate(startDate);
        week.setEndDate(endDate);

        return save(week);
    }

    @Transactional
    public Week addSession(Long weekId, Session session) {
        Week week = getById(weekId);

        if (session.getWeek() != null && !session.getWeek().getId().equals(weekId)) {
            throw new InvalidAcademicEntityException("La sesión ya está asignada a otra semana");
        }

        week.addSession(session);
        sessionService.save(session);

        return save(week);
    }

    @Transactional
    public void deleteById(Long id) {
        Week week = findById(id).orElse(null);

        if (week != null) {
            // Primero eliminar todas las sesiones asociadas
            if (week.getSessions() != null) {
                for (Session session : new ArrayList<>(week.getSessions())) {
                    entityManager.remove(session);
                }

                // Limpiar la colección
                week.getSessions().clear();
            }

            // Ahora sí eliminar la semana
            repository.deleteById(id);
        } else {
            // Corregido: Usar parámetros posicionales (?) en lugar de nombrados (:weekId)
            entityManager.createNativeQuery("DELETE FROM sessions WHERE week_id = ?")
                    .setParameter(1, id)  // Cambio de "weekId" a 1 (primer parámetro)
                    .executeUpdate();

            repository.deleteById(id);
        }
    }

    /**
     * Valida que una semana sea válida
     */
    private void validateWeek(Week week) {
        if (week == null) {
            throw new InvalidAcademicEntityException("La semana no puede ser nula");
        }

        if (week.getSection() == null) {
            throw new InvalidAcademicEntityException("La semana debe estar asociada a una sección");
        }

        if (week.getWeekNumber() == null || week.getWeekNumber() <= 0) {
            throw new InvalidAcademicEntityException("El número de semana debe ser un valor positivo");
        }

        // Validar que no exista otra semana con el mismo número para la misma sección
        if (week.getSection().getId() != null) {
            Optional<Week> existingWeek = getWeekRepository().findBySectionAndWeekNumber(
                    week.getSection(), week.getWeekNumber());

            if (existingWeek.isPresent() && !existingWeek.get().getId().equals(week.getId())) {
                throw new InvalidAcademicEntityException(
                        "Ya existe una semana " + week.getWeekNumber() +
                                " para la sección " + week.getSection().getCode());
            }
        }

        // Validar fechas si se proporcionaron
        if (week.getStartDate() != null && week.getEndDate() != null) {
            if (week.getStartDate().isAfter(week.getEndDate())) {
                throw new InvalidAcademicEntityException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
        }
    }
}