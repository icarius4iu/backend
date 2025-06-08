package pe.edu.utp.backend.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.content.model.Assignment;
import pe.edu.utp.backend.content.service.AssignmentService;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestParam Long contentId,
                                                       @RequestParam LocalDateTime dueDate,
                                                       @RequestParam String instructions,
                                                       @RequestParam Double maxGrade) {
        Assignment assignment = assignmentService.createAssignment(contentId, dueDate, instructions, maxGrade);
        return ResponseEntity.ok(assignment);
    }

    // Otros endpoints: listar, ver por sección, etc.
}