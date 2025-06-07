package pe.edu.utp.backend.auth.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponseDTO {
    private boolean success;
    private String token;
    private String email;
    private String uid;
    private String errorMessage;

    // Información adicional del estudiante (opcional)
    private String studentName;
    private String studentCode;
    private String careerName;

    /**
     * Constructor completo para todos los campos
     */
    public AuthResponseDTO(boolean success, String token, String email, String uid,
                           String errorMessage, String studentName, String studentCode,
                           String careerName) {
        this.success = success;
        this.token = token;
        this.email = email;
        this.uid = uid;
        this.errorMessage = errorMessage;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.careerName = careerName;
    }

    /**
     * Constructor para respuesta de error
     */
    public AuthResponseDTO(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor para respuesta de éxito
     */
    public AuthResponseDTO(boolean success, String token, String email, String uid,
                           String studentName, String studentCode, String careerName) {
        this.success = success;
        this.token = token;
        this.email = email;
        this.uid = uid;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.careerName = careerName;
    }
}