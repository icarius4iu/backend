package pe.edu.utp.backend.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.course.dto.SectionCreateDTO;
import pe.edu.utp.backend.course.dto.request.AssignProfessorsRequest;
import pe.edu.utp.backend.course.dto.request.DetailedSectionCreationRequest;

import pe.edu.utp.backend.course.dto.request.SectionCreateAdvancedDTO;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.*;
import pe.edu.utp.backend.course.service.composite.SectionCreationService;
import pe.edu.utp.backend.course.service.core.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;
    private final CourseService courseService;
    private final WeekService weekService;
    private final SessionService sessionService;
    private final ProfessorService professorService;
    private final SectionCreationService sectionCreationService;

    /**
     * Crea una sección completa con semanas y sesiones
     * @param dto Datos para crear la sección
     * @return La sección creada con todas sus semanas y sesiones
     */
    @PostMapping("/create-complete")
    public ResponseEntity<?> createCompleteSection(@RequestBody SectionCreateDTO dto) {
        try {
            Section section = sectionService.createCompleteSection(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(section);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/create-advanced")
    public ResponseEntity<?> createAdvancedSection(@RequestBody SectionCreateAdvancedDTO dto) {
        try {
            // Validar campos obligatorios
            if (dto.getCode() == null || dto.getCourseId() == null || dto.getCicleId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Código, curso y ciclo son campos obligatorios"));
            }

            Section section = sectionService.createAdvancedSection(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(section);
        } catch (InvalidAcademicEntityException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", "2025-06-07 13:35:26",
                    "user", "icarius4iu"
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Para depuración
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", "2025-06-07 13:35:26",
                            "user", "icarius4iu"
                    ));
        }
    }
    /**
     * Obtiene todas las secciones
     * @return Lista de secciones
     */
    @GetMapping
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sections = sectionService.findAll();
        return ResponseEntity.ok(sections);
    }

    /**
     * Obtiene una sección por su ID
     * @param id ID de la sección
     * @return La sección si existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSectionById(@PathVariable Long id) {
        Optional<Section> section = sectionService.findById(id);
        return section.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping("/{sectionId}/assign-professors")
    public ResponseEntity<Section> assignProfessorsToSection(
            @PathVariable Long sectionId,
            @RequestBody AssignProfessorsRequest request) {

        Section section = sectionService.getById(sectionId);

        // Obtén la lista de profesores y asígnalos (puedes controlar duplicados si lo deseas)
        for (Long professorId : request.getProfessorIds()) {
            Professor professor = professorService.getById(professorId);
            section.addProfessor(professor);
        }
        Section updatedSection = sectionService.save(section);
        return ResponseEntity.ok(updatedSection);
    }
    @PostMapping("/create-detailed")
    public ResponseEntity<Section> createDetailedSection(@RequestBody DetailedSectionCreationRequest req) {
        Section createdSection = sectionCreationService.createDetailedSection(req);
        return ResponseEntity.ok(createdSection);
    }
    @PostMapping("/{sectionId}/students/batch")
    public ResponseEntity<Map<String, Object>> addStudentsBatch(
            @PathVariable Long sectionId,
            @RequestBody List<Long> studentIds) {

        Map<String, Object> result = sectionService.addStudentsToSection(sectionId, studentIds);

        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
    private SectionCreateDTO convertToDTO(Section section) {
        return SectionCreateDTO.builder()
                .code(section.getCode())
                .courseId(section.getCourse() != null ? section.getCourse().getId() : null)
                .cicleId(section.getCicle() != null ? section.getCicle().getId() : null)
                .maxStudents(section.getMaxStudents())
                .virtualMeetingUrl(section.getVirtualMeetingUrl())
                .build();
    }
}