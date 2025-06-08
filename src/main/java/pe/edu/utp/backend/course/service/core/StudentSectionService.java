package pe.edu.utp.backend.course.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.backend.course.dto.request.SectionSummaryDTO;
import pe.edu.utp.backend.course.model.Section;

import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSectionService {

    private final StudentRepository studentRepository;

    public List<SectionSummaryDTO> getSectionsByStudent(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return student.getSections().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private SectionSummaryDTO toDto(Section section) {
        return new SectionSummaryDTO(
                section.getId(),
                section.getCourse().getName(),
                section.getCode(), // O usa sectionNumber si lo tienes como campo específico
                section.getModality().getDisplayName(),
                section.getProfessors().stream()
                        .map(prof -> prof.getFullName()) // Asumiendo que tienes getFullName()
                        .collect(Collectors.toList())
        );
    }
}