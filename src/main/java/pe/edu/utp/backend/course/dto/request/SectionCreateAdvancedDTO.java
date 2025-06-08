package pe.edu.utp.backend.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Section;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionCreateAdvancedDTO {
    private String code;
    private Long courseId;
    private Long cicleId;
    private Integer maxStudents;
    // Eliminamos weeksCount ya que se hereda del ciclo
    private String virtualMeetingUrl;

    private Section.Modality modality; // Nuevo campo para modalidad

    private SessionConfigDTO theorySession;
    private SessionConfigDTO labSession;
    private List<SpecialSessionDTO> specialSessions;
}