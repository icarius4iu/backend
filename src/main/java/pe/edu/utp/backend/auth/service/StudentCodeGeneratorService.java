package pe.edu.utp.backend.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.utp.backend.student.repository.StudentRepository;

import java.time.Year;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentCodeGeneratorService {
    private final StudentRepository studentRepository;
    private final Random random = new Random();

    /**
     * Genera un código único para un nuevo estudiante
     * Formato: U + AÑO + NUMERO_ALEATORIO (ej: U20251234567)
     */
    public String generateUniqueCode() {
        String baseCode = "U" + Year.now().getValue();
        int maxAttempts = 10;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String suffix = generateRandomDigits(7);
            String candidateCode = baseCode + suffix;

            if (!studentRepository.existsByStudentCode(candidateCode)) {
                log.info("Código de estudiante generado: {}", candidateCode);
                return candidateCode;
            }
        }

        // Si después de varios intentos no se encontró un código único, usa un formato más largo
        String suffix = generateRandomDigits(10);
        String candidateCode = baseCode + suffix;
        log.info("Código de estudiante generado (formato extendido): {}", candidateCode);
        return candidateCode;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}