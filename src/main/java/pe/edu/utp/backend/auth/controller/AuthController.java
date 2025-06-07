package pe.edu.utp.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.auth.dtos.AuthRequestDTO;
import pe.edu.utp.backend.auth.dtos.AuthResponseDTO;
import pe.edu.utp.backend.auth.service.FirebaseAuthService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;

    /**
     * Endpoint para la autenticación de estudiantes
     * @param request Datos de autenticación (código y contraseña)
     * @return Respuesta con token y datos del estudiante o error
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Solicitud de autenticación para estudiante con código: {}", request.getStudentCode());

        AuthResponseDTO response = firebaseAuthService.authenticate(request);

        if (response.isSuccess()) {
            log.info("Autenticación exitosa para estudiante: {}", request.getStudentCode());
            return ResponseEntity.ok(response);
        } else {
            log.warn("Autenticación fallida para estudiante: {}", request.getStudentCode());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Endpoint para verificar el estado de autenticación actual
     * @param token Token de Firebase del estudiante
     * @return Estado de la sesión
     */
    @GetMapping("/verify")
    public ResponseEntity<AuthResponseDTO> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponseDTO(false, "Token no proporcionado o inválido"));
            }

            String actualToken = token.substring(7); // Remover "Bearer "
            log.debug("Verificando token: {}", actualToken.substring(0, Math.min(10, actualToken.length())) + "...");

            // Aquí podrías implementar la verificación del token con Firebase
            // Por ahora, simplemente devolvemos una respuesta exitosa
            return ResponseEntity.ok(new AuthResponseDTO(true, "Sesión válida"));

        } catch (Exception e) {
            log.error("Error al verificar token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(false, "Token inválido"));
        }
    }
}