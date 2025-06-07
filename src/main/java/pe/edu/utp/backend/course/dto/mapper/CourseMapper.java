package pe.edu.utp.backend.course.dto.mapper;

import org.springframework.stereotype.Component;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.util.cicle.model.Cicle;

import java.util.Set;

@Component
public class CourseMapper {

    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .type(course.getType() != null ? course.getType().getDisplayName() : null)
                .credits(course.getCredits())
                .weeklyHours(course.getWeeklyHours())
                .cicleId(course.getCicle() != null ? course.getCicle().getId() : null)
                .cicleName(course.getCicle() != null ? course.getCicle().getName() : null)
                .build();
    }

    public Course toEntity(CourseCreationRequest request, Cicle cicle, Set<Career> careers) {
        if (request == null) {
            return null;
        }

        Course course = Course.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .credits(request.getCredits())
                .weeklyHours(request.getWeeklyHours())
                .cicle(cicle)
                .careers(careers)
                .build();

        return course;
    }
}