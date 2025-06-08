package pe.edu.utp.backend.schedule.dtos;



import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReprogramRequestDTO {
    private Long scheduleEntryId;
    private LocalDate newDate;
    private LocalTime newStartTime;
    private LocalTime newEndTime;      // <-- NUEVO
    private String newRoomNumber;      // <-- NUEVO
    private String reason;
}