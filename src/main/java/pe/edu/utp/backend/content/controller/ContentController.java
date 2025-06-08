package pe.edu.utp.backend.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.utp.backend.content.dtos.ContentUploadRequestDTO;
import pe.edu.utp.backend.content.dtos.ContentResponseDTO;
import pe.edu.utp.backend.content.service.ContentService;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentResponseDTO> uploadContent(
            @RequestParam("courseId") Long courseId,
            @RequestParam("sectionCode") String sectionCode,
            @RequestParam("weekNumber") Integer weekNumber,
            @RequestParam("sessionNumber") Integer sessionNumber,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("contentType") String contentType,
            @RequestParam("file") MultipartFile file) {

        ContentUploadRequestDTO request = ContentUploadRequestDTO.builder()
                .courseId(courseId)
                .sectionCode(sectionCode)
                .weekNumber(weekNumber)
                .sessionNumber(sessionNumber)
                .title(title)
                .description(description)
                .contentType(contentType)
                .file(file)
                .build();

        ContentResponseDTO response = contentService.uploadContent(request);
        return ResponseEntity.ok(response);
    }
}