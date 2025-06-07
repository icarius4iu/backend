package pe.edu.utp.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.auth.dtos.RegisterRequestDTO;
import pe.edu.utp.backend.auth.dtos.RegisterResponseDTO;
import pe.edu.utp.backend.auth.service.RegistrationService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * Endpoint para el registro de nuevos estudiantes
     * @param request Datos completos del nuevo estudiante
     * @return Respuesta con código generado o error
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerStudent(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Solicitud de registro para estudiante: {} {}", request.getFirstName(), request.getLastName());

        RegisterResponseDTO response = registrationService.registerStudent(request);

        if (response.isSuccess()) {
            log.info("Registro exitoso, código generado: {}", response.getStudentCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            log.warn("Registro fallido: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint para verificar disponibilidad de documento
     * @param documentType Tipo de documento
     * @param documentNumber Número de documento
     * @return Estado de disponibilidad
     */
    @GetMapping("/check-document")
    public ResponseEntity<Map<String, Object>> checkDocumentAvailability(
            @RequestParam String documentType,
            @RequestParam String documentNumber) {

        log.info("Verificando disponibilidad de documento: {} {}", documentType, documentNumber);

        // Aquí deberías implementar la lógica para verificar si el documento ya está registrado
        // Por ahora, simulamos una respuesta
        Map<String, Object> response = Map.of(
                "available", true,
                "message", "El documento está disponible para registro"
        );

        return ResponseEntity.ok(response);
    }
}