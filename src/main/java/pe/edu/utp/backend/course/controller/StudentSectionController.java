    package pe.edu.utp.backend.course.controller;



    import lombok.RequiredArgsConstructor;
    import org.springframework.web.bind.annotation.*;
    import pe.edu.utp.backend.course.dto.request.SectionSummaryDTO;
    import pe.edu.utp.backend.course.service.core.StudentSectionService;


    import java.util.List;

    @RestController
    @RequestMapping("/api/student-sections")
    @RequiredArgsConstructor
    public class StudentSectionController {

        private final StudentSectionService studentSectionService;

        // Ejemplo de cómo obtener el código del alumno (puedes cambiar por autenticación JWT, etc.)
        @GetMapping("/{studentCode}")
        public List<SectionSummaryDTO> getSections(@PathVariable String studentCode) {
            return studentSectionService.getSectionsByStudent(studentCode);
        }
    }