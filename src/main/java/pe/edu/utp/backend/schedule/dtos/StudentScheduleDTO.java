package pe.edu.utp.backend.schedule.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScheduleDTO {
    private Long id;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private LocalDateTime lastUpdated;
    private Boolean notificationEnabled;
    private List<ScheduleEntryDTO> entries;
}
