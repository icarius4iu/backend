package pe.edu.utp.backend.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialSessionDTO {
    private Integer weekNumber;
    private Integer sessionNumber;
    private String title;
    private String description;
}