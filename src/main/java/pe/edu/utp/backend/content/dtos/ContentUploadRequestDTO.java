package pe.edu.utp.backend.content.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentUploadRequestDTO {
    private Long courseId;
    private String sectionCode;
    private Integer weekNumber;
    private Integer sessionNumber;
    private String title;
    private String description;
    private String contentType; // "MATERIAL", "ASSIGNMENT", "QUIZ", etc.

    // Este campo no se serializa como JSON, vendrá como part del MultipartFile
    private transient MultipartFile file;
}