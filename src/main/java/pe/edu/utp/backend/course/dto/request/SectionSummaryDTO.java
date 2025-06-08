package pe.edu.utp.backend.course.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SectionSummaryDTO {
    private Long sectionId;
    private String courseName;
    private String sectionNumber;
    private String modality;
    private List<String> professorNames;
}