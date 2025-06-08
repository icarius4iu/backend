package pe.edu.utp.backend.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.course.model.Section; // importa el enum

import java.time.LocalDate;

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

        // Opcional: añadir una fecha de inicio para las sesiones
        private LocalDate startDate;

        // Modalidad de la sección (nuevo)
        private Section.Modality modality;
}