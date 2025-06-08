package pe.edu.utp.backend.schedule.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayScheduleDTO {
    private LocalDate date;
    private DayOfWeek dayOfWeek;

    @Builder.Default
    private List<ScheduleEntryDTO> entries = new ArrayList<>();
}