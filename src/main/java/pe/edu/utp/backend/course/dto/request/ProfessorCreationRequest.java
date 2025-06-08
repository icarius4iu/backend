package pe.edu.utp.backend.course.dto.request;

import lombok.Data;

@Data
public class ProfessorCreationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String specialization;
    private String department;
    private String professorCode;
}