package pe.edu.utp.backend.course.validator;

import org.springframework.stereotype.Component;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.repository.CourseRepository;

@Component
public class CourseValidator {

    private final CourseRepository courseRepository;

    public CourseValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void validateNewCourse(Course course) {
        if (course == null) {
            throw new InvalidAcademicEntityException("El curso no puede ser nulo");
        }

        if (course.getCode() == null || course.getCode().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El código del curso es obligatorio");
        }

        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El nombre del curso es obligatorio");
        }

        // Eliminado: validación de tipo/modality, ya no corresponde a Course

        if (course.getCredits() == null || course.getCredits() <= 0) {
            throw new InvalidAcademicEntityException("El número de créditos debe ser un valor positivo");
        }

        if (course.getWeeklyHours() == null || course.getWeeklyHours() <= 0) {
            throw new InvalidAcademicEntityException("Las horas semanales deben ser un valor positivo");
        }

        if (course.getCicle() == null) {
            throw new InvalidAcademicEntityException("El ciclo académico es obligatorio");
        }

        // Verificar si ya existe un curso con el mismo código
        if (courseRepository.existsByCode(course.getCode())) {
            throw new InvalidAcademicEntityException(
                    "Ya existe un curso con el código " + course.getCode());
        }
    }

    public void validateExistingCourse(Course course) {
        if (course == null) {
            throw new InvalidAcademicEntityException("El curso no puede ser nulo");
        }

        if (course.getId() == null) {
            throw new InvalidAcademicEntityException("El ID del curso es obligatorio para actualización");
        }

        // Las demás validaciones son similares a validateNewCourse pero omitimos la verificación
        // de código duplicado o implementamos una lógica que excluya el propio curso
    }
}