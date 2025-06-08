package pe.edu.utp.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.utp.backend.content.model.Assignment;
import pe.edu.utp.backend.content.model.AssignmentSubmission;
import pe.edu.utp.backend.content.repository.AssignmentRepository;
import pe.edu.utp.backend.content.repository.AssignmentSubmissionRepository;
import pe.edu.utp.backend.storage.service.FirebaseStorageService;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.repository.StudentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final FirebaseStorageService firebaseStorageService;

    public AssignmentSubmission submitAssignment(Long assignmentId, Long studentId, MultipartFile file) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        LocalDateTime now = LocalDateTime.now();
        boolean isLate = assignment.getDueDate() != null && now.isAfter(assignment.getDueDate());

        // Sube archivo a Firebase Storage
        String fileUrl = firebaseStorageService.upload(file);

        // Permitir reentrega: busca si ya existe una entrega de este estudiante para esta tarea
        AssignmentSubmission submission = submissionRepository
                .findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElse(null);

        if (submission == null) {
            // Primera entrega
            submission = AssignmentSubmission.builder()
                    .assignment(assignment)
                    .student(student)
                    .build();
        }
        // Actualiza datos del archivo, fecha, etc.
        submission.setFileUrl(fileUrl);
        submission.setFileName(file.getOriginalFilename());
        submission.setFileSize(file.getSize());
        submission.setSubmittedAt(now);
        // submission.setLate(isLate); // Si tienes este campo

        return submissionRepository.save(submission);
    }
}