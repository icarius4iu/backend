package pe.edu.utp.backend.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Section;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionCreateDTO {
    private String code;
    private Long courseId;
    private Long cicleId;
    private Integer maxStudents;
    private Integer weeksCount;
    private Integer sessionsPerWeek;
    private String virtualMeetingUrl;
    private Section.Modality modality; // Nuevo campo para modalidad (enum)
}