package pe.edu.utp.backend.schedule.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String meetingUrl;
    private String courseCode;
    private String courseName;
    private String sectionCode;
    private String entryType;
    private Boolean hasEvaluation;
    private String backgroundColor;
    private LocalDate sessionDate;
}