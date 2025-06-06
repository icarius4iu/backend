package pe.edu.utp.backend.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Course;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreationRequest {
    @NotBlank(message = "El código del curso es obligatorio")
    private String code;

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El tipo de curso es obligatorio")
    private Course.CourseType type;

    @NotNull(message = "El número de créditos es obligatorio")
    @Min(value = 1, message = "El número de créditos debe ser al menos 1")
    private Integer credits;

    @NotNull(message = "Las horas semanales son obligatorias")
    @Min(value = 1, message = "Las horas semanales deben ser al menos 1")
    private Integer weeklyHours;

    @NotNull(message = "El ID del ciclo académico es obligatorio")
    private Long cicleId;

    private List<Long> careerIds;
}