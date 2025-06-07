package pe.edu.utp.backend.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyTokenResponseDTO {
    private boolean success;
    private String message;
    private String userId;
}