package pe.edu.utp.backend.course.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CourseCreationRequestDTO {
    private String code;
    private Integer credits;
    private String description;
    private String name;
    // private String type; // <-- ELIMINADO: la modalidad ya no va en el curso
    private Integer weeklyHours;
    private Long cicleId; // ID del ciclo académico
    private List<Long> careerIds; // IDs de las carreras asociadas
}