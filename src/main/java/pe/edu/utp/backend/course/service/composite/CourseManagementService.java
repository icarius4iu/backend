package pe.edu.utp.backend.course.service.composite;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.exception.CourseException;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.service.core.CourseService;
import pe.edu.utp.backend.course.service.core.ProfessorService;
import pe.edu.utp.backend.course.service.core.SectionService;
import pe.edu.utp.backend.course.service.core.WeekService;
import pe.edu.utp.backend.util.cicle.Cicle;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CourseManagementService {

    private final CourseService courseService;
    private final SectionService sectionService;
    private final ProfessorService professorService;
    private final WeekService weekService;
    private final CareerRepository careerRepository;

    public CourseManagementService(
            CourseService courseService,
            SectionService sectionService,
            ProfessorService professorService,
            WeekService weekService,
            CareerRepository careerRepository) {
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.professorService = professorService;
        this.weekService = weekService;
        this.careerRepository = careerRepository;
    }

    /**
     * Crea un curso completo con secciones y asigna profesores
     */
    public Course createCourseWithSections(
            CourseCreationRequest courseRequest,
            int numSections,
            int maxStudentsPerSection,
            List<Long> professorIds) {

        // 1. Crear el curso
        CourseDTO courseDTO = courseService.createCourseFromRequest(courseRequest);
        Course course = courseService.getById(courseDTO.getId());

        // 2. Crear secciones
        for (int i = 0; i < numSections; i++) {
            // Crear sección con letra (A, B, C, etc.)
            String sectionCode = String.valueOf((char) ('A' + i));

            Section section = Section.builder()
                    .code(sectionCode)
                    .course(course)
                    .maxStudents(maxStudentsPerSection)
                    .build();

            // Guardar sección
            Section savedSection = sectionService.createSection(section);

            // Generar semanas automaticamente
            sectionService.generateWeeks(savedSection.getId());

            // Agregar la sección al curso
            course.addSection(savedSection);
        }

        // 3. Asignar profesores si se proporcionaron
        if (professorIds != null && !professorIds.isEmpty()) {
            assignProfessorsToSections(course, professorIds);
        }

        return courseService.save(course);
    }

    /**
     * Asigna profesores a las secciones del curso
     */
    public Course assignProfessorsToSections(Course course, List<Long> professorIds) {
        if (course.getSections().isEmpty()) {
            throw new CourseException("El curso no tiene secciones para asignar profesores");
        }

        if (professorIds.isEmpty()) {
            throw new CourseException("No se proporcionaron profesores para asignar");
        }

        // Obtener profesores
        List<Professor> professors = professorIds.stream()
                .map(id -> professorService.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "No se encontró el profesor con ID: " + id)))
                .toList();

        // Asignar profesores a las secciones (distribuyendo de forma circular si hay más secciones que profesores)
        int professorIndex = 0;
        for (Section section : course.getSections()) {
            Professor professor = professors.get(professorIndex % professors.size());
            section.addProfessor(professor);
            sectionService.save(section);
            professorIndex++;
        }

        return courseService.getById(course.getId());
    }

    /**
     * Agrega un curso a múltiples carreras
     */
    public Course addCourseToCarreras(Long courseId, List<Long> careerIds) {
        Course course = courseService.getById(courseId);

        for (Long careerId : careerIds) {
            Career career = careerRepository.findById(careerId)
                    .orElseThrow(() -> new InvalidAcademicEntityException(
                            "No se encontró la carrera con ID: " + careerId));

            course.addCareer(career);
        }

        return courseService.save(course);
    }

    /**
     * Actualiza las fechas de todas las semanas de un curso según el ciclo
     */
    public Course updateWeekDates(Long courseId) {
        Course course = courseService.getById(courseId);
        Cicle cicle = course.getCicle();

        if (cicle == null) {
            throw new CourseException("El curso no tiene un ciclo asignado");
        }

        LocalDate startDate = cicle.getStartDate();
        LocalDate endDate = cicle.getEndDate();

        if (startDate == null || endDate == null) {
            throw new CourseException("El ciclo no tiene fechas de inicio o fin definidas");
        }

        int totalWeeks = cicle.getWeeksCount();
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        int daysPerWeek = (int) (totalDays / totalWeeks);

        // Actualizar fechas para cada semana en cada sección
        for (Section section : course.getSections()) {
            List<Week> weeks = weekService.findWeeksBySection(section);

            for (int i = 0; i < weeks.size(); i++) {
                Week week = weeks.get(i);
                LocalDate weekStart = startDate.plusDays(i * daysPerWeek);
                LocalDate weekEnd = i < weeks.size() - 1 ?
                        startDate.plusDays((i + 1) * daysPerWeek - 1) : endDate;

                week.setStartDate(weekStart);
                week.setEndDate(weekEnd);
                weekService.save(week);
            }
        }

        return courseService.getById(courseId);
    }
}