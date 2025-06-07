package pe.edu.utp.backend.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private boolean success;
    private String studentCode;
    private String message;
}