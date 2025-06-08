package pe.edu.utp.backend.schedule.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long studentId;
    private String studentName;
    private LocalDate startDate;
    private LocalDate endDate;


    @Builder.Default
    private List<DayScheduleDTO> days = new ArrayList<>();
}