package pe.edu.utp.backend.storage.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateCourseRequest {
    private String code;
    private int credits;
    private String description;
    private String name;
    private int weeklyHours;
    private Long cicleId;
    private List<Long> careerIds;
}