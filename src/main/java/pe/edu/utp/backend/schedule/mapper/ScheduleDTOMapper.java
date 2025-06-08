package pe.edu.utp.backend.schedule.mapper;

import org.springframework.stereotype.Component;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.schedule.dtos.ScheduleEntryDTO;
import pe.edu.utp.backend.schedule.dtos.StudentScheduleDTO;
import pe.edu.utp.backend.schedule.model.ScheduleEntry;
import pe.edu.utp.backend.schedule.model.StudentSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleDTOMapper {

    /**
     * Convierte una entidad StudentSchedule a su DTO correspondiente
     */
    public StudentScheduleDTO toDTO(StudentSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        // Construir el nombre del estudiante si está disponible
        String studentName = null;
        if (schedule.getStudent() != null) {
            if (schedule.getStudent().getInformation() != null) {
                studentName = schedule.getStudent().getInformation().getFirstName() + " " +
                        schedule.getStudent().getInformation().getLastName();
            } else {
                studentName = "Estudiante " + schedule.getStudent().getStudentCode();
            }
        }

        return StudentScheduleDTO.builder()
                .id(schedule.getId())
                .studentId(schedule.getStudent() != null ? schedule.getStudent().getId() : null)
                .studentCode(schedule.getStudent() != null ? schedule.getStudent().getStudentCode() : null)
                .studentName(studentName)
                .lastUpdated(schedule.getLastUpdated())
                .notificationEnabled(schedule.getNotificationEnabled())
                .entries(toEntryDTOList(new ArrayList<>(schedule.getEntries())))
                .build();
    }

    /**
     * Convierte una lista de entidades ScheduleEntry a una lista de DTOs
     */
    public List<ScheduleEntryDTO> toEntryDTOList(List<ScheduleEntry> entries) {
        if (entries == null) {
            return new ArrayList<>();
        }

        return entries.stream()
                .map(this::toEntryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad ScheduleEntry a su DTO correspondiente
     */
    public ScheduleEntryDTO toEntryDTO(ScheduleEntry entry) {
        if (entry == null) {
            return null;
        }

        // Extraer información del curso y sección si están disponibles
        String courseCode = null;
        String courseName = null;
        String sectionCode = null;

        Section section = entry.getSection();
        if (section != null && section.getCourse() != null) {
            courseCode = section.getCourse().getCode();
            courseName = section.getCourse().getName();
            sectionCode = section.getCode();
        }

        String backgroundColor = getBackgroundColorForEntryType(entry.getEntryType());

        return ScheduleEntryDTO.builder()
                .id(entry.getId())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .location(entry.getLocation())
                .meetingUrl(entry.getMeetingUrl())
                .courseCode(courseCode)
                .courseName(courseName)
                .sectionCode(sectionCode)
                .entryType(entry.getEntryType() != null ? entry.getEntryType().getDisplayName() : null)
                .hasEvaluation(entry.getHasEvaluation())
                .backgroundColor(backgroundColor)
                .sessionDate(entry.getSession() != null ? entry.getSession().getSessionDate() : null) // <-- AGREGADO AQUÍ
                .build();
    }
    /**
     * Define colores para los distintos tipos de entradas en el horario
     */
    private String getBackgroundColorForEntryType(ScheduleEntry.EntryType entryType) {
        if (entryType == null) {
            return "#B0BEC5"; // Gris por defecto
        }

        switch (entryType) {
            case CLASS_SESSION:
                return "#42A5F5"; // Azul
            case EXAM:
                return "#EF5350"; // Rojo
            case LAB:
                return "#66BB6A"; // Verde
            case WORKSHOP:
                return "#FFA726"; // Naranja
            case DEADLINE:
                return "#AB47BC"; // Púrpura
            case PERSONAL:
                return "#26A69A"; // Verde azulado
            default:
                return "#B0BEC5"; // Gris por defecto
        }
    }
}