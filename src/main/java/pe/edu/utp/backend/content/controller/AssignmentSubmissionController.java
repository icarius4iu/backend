package pe.edu.utp.backend.content.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.utp.backend.content.model.AssignmentSubmission;
import pe.edu.utp.backend.content.service.AssignmentSubmissionService;


@RestController
@RequestMapping("/api/assignment-submissions")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<AssignmentSubmission> submitAssignment(@RequestParam Long assignmentId,
                                                                 @RequestParam Long studentId,
                                                                 @RequestParam MultipartFile file) {
        AssignmentSubmission submission = submissionService.submitAssignment(assignmentId, studentId, file);
        return ResponseEntity.ok(submission);
    }


}