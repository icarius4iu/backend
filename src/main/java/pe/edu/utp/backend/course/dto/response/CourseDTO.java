package pe.edu.utp.backend.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Course;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private Integer weeklyHours;
    private Long cicleId;
    private String cicleName;
}