package pe.edu.utp.backend.content.dtos;

import lombok.Data;
import pe.edu.utp.backend.content.model.Content.ContentType;
import pe.edu.utp.backend.content.model.Content.ResourceType;
import pe.edu.utp.backend.content.model.Content.FileType;

@Data
public class ContentRequestDTO {
    private String title;
    private String description;
    private ContentType contentType;
    private ResourceType resourceType;
    private FileType fileType;

    private Long courseId;
    private Long sectionId;
    private Long weekId;
    private Long sessionId;

    private String fileUrl;        // Para enlaces externos o archivos ya subidos
    private String fileName;
    private Long fileSize;

    private Boolean published;
    private String createdBy;
}