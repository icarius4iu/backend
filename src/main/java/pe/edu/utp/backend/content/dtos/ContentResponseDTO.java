package pe.edu.utp.backend.content.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDTO {
    private Long id;
    private String title;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private LocalDateTime createdAt;
}