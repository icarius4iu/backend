package pe.edu.utp.backend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pe.edu.utp.backend.auth.dtos.AuthRequestDTO;
import pe.edu.utp.backend.auth.dtos.AuthResponseDTO;
import pe.edu.utp.backend.auth.dtos.VerifyTokenResponseDTO;
import pe.edu.utp.backend.auth.model.StudentCredentials;
import pe.edu.utp.backend.auth.repository.StudentCredentialsRepository;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.repository.StudentRepository;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class FirebaseAuthService {
    private final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";
    private final String API_KEY;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StudentCredentialsRepository credentialsRepository;
    private final StudentRepository studentRepository;

    public FirebaseAuthService(
            @Value("${firebase.api.key}") String apiKey,
            StudentCredentialsRepository credentialsRepository,
            StudentRepository studentRepository) {
        this.API_KEY = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.credentialsRepository = credentialsRepository;
        this.studentRepository = studentRepository;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        try {
            // Convertir código a email institucional
            String email = generateInstitutionalEmail(request.getStudentCode());
            log.debug("Intentando autenticación para código: {} con email: {}",
                    request.getStudentCode(), email);

            // Verificar si el estudiante existe en nuestro sistema
            Optional<Student> studentOpt = studentRepository.findByStudentCode(request.getStudentCode());
            if (studentOpt.isEmpty()) {
                return new AuthResponseDTO(false, null, null, null,
                        "El código de estudiante no existe en el sistema",
                        null, null, null);
            }

            Student student = studentOpt.get();

            // Verificar estado del estudiante
            if (student.getStatus() != Student.Status.ACTIVO) {
                return new AuthResponseDTO(false, null, null, null,
                        "La cuenta de estudiante no está activa",
                        null, null, null);
            }

            // Autenticar con Firebase
            String url = FIREBASE_AUTH_URL + "?key=" + API_KEY;
            Map<String, String> firebaseRequest = Map.of(
                    "email", email,
                    "password", request.getPassword(),
                    "returnSecureToken", "true"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(url, firebaseRequest, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Actualizar UID de Firebase si es necesario
                String firebaseUid = (String) response.getBody().get("localId");
                updateFirebaseUid(student, firebaseUid);

                return new AuthResponseDTO(
                        true,
                        (String) response.getBody().get("idToken"),
                        email,
                        firebaseUid,
                        null,
                        student.getFullName(),
                        student.getStudentCode(),
                        student.getCareer() != null ? student.getCareer().getName() : null
                );
            }

            return new AuthResponseDTO(false, null, null, null,
                    "Autenticación fallida", null, null, null);

        } catch (HttpClientErrorException e) {
            log.error("Error de autenticación: {}", e.getResponseBodyAsString());
            return extractFirebaseError(e);
        } catch (Exception e) {
            log.error("Error inesperado durante autenticación", e);
            return new AuthResponseDTO(false, null, null, null,
                    "Error inesperado durante la autenticación",
                    null, null, null);
        }
    }

    /**
     * Verifica la validez de un token JWT de Firebase
     * @param token El token JWT a verificar
     * @return Respuesta con el resultado de la verificación
     */
    public VerifyTokenResponseDTO verifyToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return new VerifyTokenResponseDTO(false, "Token no proporcionado", null);
            }

            // Verificar el token con Firebase Admin SDK
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Obtener el UID del token decodificado
            String uid = decodedToken.getUid();

            // Verificar si el UID existe en nuestro sistema
            Optional<StudentCredentials> credentials = credentialsRepository.findByFirebaseUid(uid);
            if (credentials.isEmpty()) {
                return new VerifyTokenResponseDTO(false, "Usuario no encontrado en el sistema", null);
            }

            // Verificar si el estudiante está activo
            Student student = credentials.get().getStudent();
            if (student.getStatus() != Student.Status.ACTIVO) {
                return new VerifyTokenResponseDTO(false, "La cuenta de estudiante no está activa", null);
            }

            return new VerifyTokenResponseDTO(true, "Sesión válida", uid);

        } catch (FirebaseAuthException e) {
            log.error("Error al verificar token: {}", e.getMessage());

            // Determinar el tipo de error para dar un mensaje más específico
            String errorCode = e.getAuthErrorCode().name();
            String errorMessage = switch (errorCode) {
                case "EXPIRED_ID_TOKEN" -> "La sesión ha expirado. Por favor, inicie sesión nuevamente.";
                case "INVALID_ID_TOKEN" -> "Token inválido.";
                case "REVOKED_ID_TOKEN" -> "La sesión fue revocada. Por favor, inicie sesión nuevamente.";
                default -> "Error al verificar la sesión: " + e.getMessage();
            };

            return new VerifyTokenResponseDTO(false, errorMessage, null);
        } catch (Exception e) {
            log.error("Error inesperado al verificar token", e);
            return new VerifyTokenResponseDTO(false, "Error inesperado al verificar la sesión", null);
        }
    }

    private String generateInstitutionalEmail(String studentCode) {
        return studentCode.toLowerCase() + "@utp.edu.pe";
    }

    private void updateFirebaseUid(Student student, String firebaseUid) {
        Optional<StudentCredentials> credentialsOpt = credentialsRepository.findByStudent(student);
        if (credentialsOpt.isPresent()) {
            StudentCredentials credentials = credentialsOpt.get();
            if (credentials.getFirebaseUid() == null ||
                    !credentials.getFirebaseUid().equals(firebaseUid)) {
                credentials.setFirebaseUid(firebaseUid);
                credentialsRepository.save(credentials);
                log.debug("Actualizado UID de Firebase para estudiante: {}", student.getStudentCode());
            }
        }
    }

    private AuthResponseDTO extractFirebaseError(HttpClientErrorException e) {
        try {
            Map<String, Object> errorBody = objectMapper.readValue(
                    e.getResponseBodyAsString(),
                    Map.class
            );

            Map<String, Object> error = (Map<String, Object>) errorBody.get("error");
            String message = getFirebaseErrorMessage((String) error.get("message"));

            log.debug("Código de error de Firebase: {}", error.get("message"));
            return new AuthResponseDTO(false, null, null, null, message, null, null, null);

        } catch (Exception ex) {
            log.error("Error al analizar respuesta de error de Firebase", ex);
            return new AuthResponseDTO(false, null, null, null, "Error de autenticación", null, null, null);
        }
    }

    private String getFirebaseErrorMessage(String errorCode) {
        return switch (errorCode) {
            case "INVALID_LOGIN_CREDENTIALS" -> "Credenciales inválidas. Por favor, verifique su código y contraseña";
            case "EMAIL_NOT_FOUND" -> "El correo electrónico no está registrado";
            case "INVALID_PASSWORD" -> "La contraseña es incorrecta";
            case "USER_DISABLED" -> "La cuenta ha sido deshabilitada";
            case "EMAIL_EXISTS" -> "El correo electrónico ya está en uso";
            case "OPERATION_NOT_ALLOWED" -> "El inicio de sesión está deshabilitado";
            case "TOO_MANY_ATTEMPTS_TRY_LATER" -> "Demasiados intentos fallidos. Intente más tarde";
            default -> "Error de autenticación: " + errorCode;
        };
    }
}