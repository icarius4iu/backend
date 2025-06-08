package pe.edu.utp.backend.course.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Session;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionConfigDTO {
    private Session.SessionType type;
    private String defaultTitle;
    private String description;
    private String buildingName;
    private String roomNumber;
    private List<ScheduleDayDTO> scheduleDays;
}