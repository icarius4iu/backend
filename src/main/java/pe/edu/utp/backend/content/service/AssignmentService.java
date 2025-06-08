package pe.edu.utp.backend.content.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.backend.content.model.Assignment;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.content.repository.AssignmentRepository;
import pe.edu.utp.backend.content.repository.ContentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ContentRepository contentRepository;

    public Assignment createAssignment(Long contentId, LocalDateTime dueDate, String instructions, Double maxGrade) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));
        Assignment assignment = Assignment.builder()
                .content(content)
                .dueDate(dueDate)
                .instructions(instructions)
                .maxGrade(maxGrade)
                .build();
        return assignmentRepository.save(assignment);
    }


}
