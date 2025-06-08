package pe.edu.utp.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.utp.backend.content.dtos.ContentUploadRequestDTO;
import pe.edu.utp.backend.content.dtos.ContentResponseDTO;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.content.repository.ContentRepository;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Session;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.storage.service.FirebaseStorageService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Transactional
    public ContentResponseDTO uploadContent(ContentUploadRequestDTO request) {
        // 1. Validar y obtener curso, sección, semana y sesión
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Section section = sectionRepository.findByCourseIdAndCode(request.getCourseId(), request.getSectionCode())
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        Week week = section.getWeeks().stream()
                .filter(w -> w.getWeekNumber().equals(request.getWeekNumber()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Semana no encontrada"));

        // 2. Construir la ruta de almacenamiento
        String storagePath = String.format("courses/%s_%s/sections/%s/week_%d/session_%d/",
                course.getCode(), sanitize(course.getName()),
                section.getCode(), request.getWeekNumber(), request.getSessionNumber());

        // 3. Subir el archivo a Firebase Storage
        String fileName = sanitizeFileName(request.getFile().getOriginalFilename());
        String fileUrl = firebaseStorageService.uploadFile(request.getFile(), storagePath + fileName);

        // 4. Crear y guardar el registro de contenido en la base de datos
        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileSize(request.getFile().getSize())
                .mimeType(request.getFile().getContentType())
                .course(course)
                .section(section)
                .week(week)
                .weekNumber(request.getWeekNumber())
                .sessionNumber(request.getSessionNumber())
                .createdAt(LocalDateTime.now())
                .build();

        contentRepository.save(content);

        // 5. Devolver la respuesta
        return ContentResponseDTO.builder()
                .id(content.getId())
                .title(content.getTitle())
                .fileUrl(content.getFileUrl())
                .fileName(content.getFileName())
                .contentType(content.getContentType())
                .createdAt(content.getCreatedAt())
                .build();
    }

    private String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unnamed_file";
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}