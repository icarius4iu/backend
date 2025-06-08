package pe.edu.utp.backend.course.service.core;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.course.dto.mapper.CourseMapper;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;
import pe.edu.utp.backend.course.validator.CourseValidator;
import pe.edu.utp.backend.util.cicle.model.Cicle;
import pe.edu.utp.backend.util.cicle.repository.CicleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService extends BaseAcademicService<Course, Long> {

    private final CourseValidator courseValidator;
    private final CourseMapper courseMapper;
    private final CicleRepository cicleRepository;
    private final CareerRepository careerRepository;

    public CourseService(
            CourseRepository courseRepository,
            CourseValidator courseValidator,
            CourseMapper courseMapper,
            CicleRepository cicleRepository,
            CareerRepository careerRepository) {
        super(courseRepository);
        this.courseValidator = courseValidator;
        this.courseMapper = courseMapper;
        this.cicleRepository = cicleRepository;
        this.careerRepository = careerRepository;
    }

    public CourseRepository getCourseRepository() {
        return (CourseRepository) repository;
    }

    @Transactional
    public Course createCourse(Course course) {
        courseValidator.validateNewCourse(course);
        return save(course);
    }

    @Transactional
    public CourseDTO createCourseFromRequest(CourseCreationRequest request) {
        // Obtener el ciclo
        Cicle cicle = cicleRepository.findById(request.getCicleId())
                .orElseThrow(() -> new InvalidAcademicEntityException(
                        "No se encontró el ciclo con ID: " + request.getCicleId()));

        // Obtener las carreras si se especificaron
        Set<Career> careers = new HashSet<>();
        if (request.getCareerIds() != null && !request.getCareerIds().isEmpty()) {
            careers = request.getCareerIds().stream()
                    .map(id -> careerRepository.findById(id)
                            .orElseThrow(() -> new InvalidAcademicEntityException(
                                    "No se encontró la carrera con ID: " + id)))
                    .collect(Collectors.toSet());
        }

        // Convertir request a entidad
        Course course = courseMapper.toEntity(request, cicle, careers);

        // Validar y guardar
        courseValidator.validateNewCourse(course);
        Course savedCourse = save(course);

        // Convertir a DTO y devolver
        return courseMapper.toDTO(savedCourse);
    }

    @Transactional
    public Course updateCourse(Course course) {
        courseValidator.validateExistingCourse(course);
        return save(course);
    }

    // Eliminado: findCoursesByType, ya que el tipo/modality ahora corresponde a Section

    public List<Course> findCoursesByCicle(Cicle cicle) {
        return getCourseRepository().findByCicle(cicle);
    }

    public List<Course> findCoursesByCareer(Career career) {
        return getCourseRepository().findByCareersContaining(career);
    }

    public List<Course> findCoursesWithAvailableSections() {
        return getCourseRepository().findCoursesWithAvailableSections();
    }

    public Course findByCode(String code) {
        return getCourseRepository().findByCode(code)
                .orElseThrow(() -> new InvalidAcademicEntityException(
                        "No se encontró un curso con el código: " + code));
    }
}