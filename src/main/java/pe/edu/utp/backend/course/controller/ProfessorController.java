package pe.edu.utp.backend.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.course.dto.request.ProfessorCreationRequest;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.service.core.ProfessorService;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping("/create")
    public ResponseEntity<Professor> createProfessor(@RequestBody ProfessorCreationRequest request) {
        Professor professor = Professor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .specialization(request.getSpecialization())
                .department(request.getDepartment())
                .professorCode(request.getProfessorCode())
                .build();

        Professor saved = professorService.createProfessor(professor);
        return ResponseEntity.ok(saved);
    }
}