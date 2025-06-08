package pe.edu.utp.backend.course.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AssignProfessorsRequest {
    private List<Long> professorIds;
}