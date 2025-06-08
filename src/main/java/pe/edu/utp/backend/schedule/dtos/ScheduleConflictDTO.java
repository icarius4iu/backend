package pe.edu.utp.backend.schedule.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictDTO {
    private String existingCourse;
    private String existingSection;
    private String existingTime;
    private String newSession;
    private String newTime;
    private String day;
    private String conflictType;

    public String getConflictDescription() {
        if ("OVERLAP".equals(conflictType)) {
            return "Superposición de horarios: " + existingCourse + " " + existingTime +
                    " y " + newSession + " " + newTime + " (" + day + ")";
        } else if (conflictType.startsWith("PROXIMITY")) {
            return "Clases muy cercanas: " + existingCourse + " " + existingTime +
                    " y " + newSession + " " + newTime + " (" + day + ")";
        }
        return "Conflicto de horario";
    }
}