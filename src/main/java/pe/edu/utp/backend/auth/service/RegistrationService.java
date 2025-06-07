package pe.edu.utp.backend.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pe.edu.utp.backend.auth.dtos.RegisterRequestDTO;
import pe.edu.utp.backend.auth.dtos.RegisterResponseDTO;
import pe.edu.utp.backend.auth.model.StudentCredentials;
import pe.edu.utp.backend.auth.repository.StudentCredentialsRepository;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.service.StudentInformationService;
import pe.edu.utp.backend.student.service.StudentService;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class RegistrationService {
    private static final String FIREBASE_SIGNUP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp";
    private final String API_KEY;
    private final RestTemplate restTemplate;
    private final StudentService studentService;
    private final StudentInformationService studentInformationService;
    private final CareerRepository careerRepository;
    private final StudentCredentialsRepository credentialsRepository;
    private final StudentCodeGeneratorService studentCodeGeneratorService;

    public RegistrationService(
            @Value("${firebase.api.key}") String apiKey,
            StudentService studentService,
            StudentInformationService studentInformationService,
            CareerRepository careerRepository,
            StudentCredentialsRepository credentialsRepository,
            StudentCodeGeneratorService studentCodeGeneratorService) {
        this.API_KEY = apiKey;
        this.restTemplate = new RestTemplate();
        this.studentService = studentService;
        this.studentInformationService = studentInformationService;
        this.careerRepository = careerRepository;
        this.credentialsRepository = credentialsRepository;
        this.studentCodeGeneratorService = studentCodeGeneratorService;
    }

    @Transactional
    public RegisterResponseDTO registerStudent(RegisterRequestDTO request) {
        try {
            log.info("Iniciando proceso de registro para estudiante con documento: {}", request.getDocumentNumber());

            // 1. Validar documento
            if (studentInformationService.existsByDocument(request.getDocumentNumber())) {
                log.warn("Intento de registro con documento ya existente: {}", request.getDocumentNumber());
                return new RegisterResponseDTO(false, null,
                        "Ya existe un estudiante con ese número de documento");
            }

            // 2. Validar email personal
            if (studentInformationService.existsByEmail(request.getPersonalEmail())) {
                log.warn("Intento de registro con email ya existente: {}", request.getPersonalEmail());
                return new RegisterResponseDTO(false, null,
                        "El correo electrónico personal ya está registrado");
            }

            // 3. Validar carrera
            Optional<Career> careerOpt = careerRepository.findById(request.getCareerId());
            if (careerOpt.isEmpty()) {
                log.warn("Intento de registro con carrera inexistente ID: {}", request.getCareerId());
                return new RegisterResponseDTO(false, null,
                        "La carrera seleccionada no existe");
            }
            Career career = careerOpt.get();
            log.info("Carrera validada: {}", career.getName());

            // 4. Generar código y correo institucional
            String studentCode = studentCodeGeneratorService.generateUniqueCode();
            String institutionalEmail = generateInstitutionalEmail(studentCode);
            log.info("Código generado: {}, email institucional: {}", studentCode, institutionalEmail);

            // 5. Registrar en Firebase
            try {
                String url = FIREBASE_SIGNUP_URL + "?key=" + API_KEY;
                Map<String, String> firebaseRequest = Map.of(
                        "email", institutionalEmail,
                        "password", request.getPassword(),
                        "returnSecureToken", "true"
                );

                log.debug("Enviando solicitud a Firebase para registro de usuario");
                ResponseEntity<Map> response = restTemplate.postForEntity(url, firebaseRequest, Map.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String firebaseUid = (String) response.getBody().get("localId");
                    log.info("Usuario registrado en Firebase con UID: {}", firebaseUid);

                    // 6. Crear y guardar entidades
                    // Primero el estudiante - Asignación correcta de faculty desde career
                    Student student = Student.builder()
                            .studentCode(studentCode)
                            .status(Student.Status.ACTIVO)
                            .career(career)
                            .faculty(career.getFaculty()) // Asignar el String faculty de Career
                            .modality(request.getModality())
                            .campus(request.getCampus())
                            .enrollmentDate(LocalDate.now())
                            .lastRegistrationDate(LocalDate.now())
                            // last_enrollment_date queda null hasta que se matricule
                            .build();

                    log.debug("Guardando entidad Student");
                    Student savedStudent = studentService.save(student);

                    // Luego la información del estudiante con todos los campos
                    StudentInformation information = StudentInformation.builder()
                            .student(savedStudent)
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .motherLastName(request.getMotherLastName())
                            .documentType(request.getDocumentType())
                            .documentNumber(request.getDocumentNumber())
                            .birthDate(request.getBirthDate())
                            .civilStatus(request.getCivilStatus())
                            .address(request.getAddress())
                            .district(request.getDistrict())
                            .province(request.getProvince())
                            .department(request.getDepartment())
                            .emergencyContactName(request.getEmergencyContactName())
                            .emergencyContactRelationship(request.getEmergencyContactRelationship())
                            .emergencyContactPhone(request.getEmergencyContactPhone())
                            .emergencyContactAddress(request.getEmergencyContactAddress())
                            .landlinePhone(request.getLandlinePhone())
                            .mobilePhone(request.getMobilePhone())
                            .personalEmail(request.getPersonalEmail())
                            .build();

                    log.debug("Guardando entidad StudentInformation");
                    studentInformationService.save(information);

                    // Establecer relación bidireccional
                    savedStudent.setInformation(information);
                    studentService.save(savedStudent);

                    // Finalmente, guardar las credenciales
                    StudentCredentials credentials = StudentCredentials.builder()
                            .student(savedStudent)
                            .status("ACTIVO")
                            .firebaseUid(firebaseUid)
                            .password(request.getPassword())
                            .build();

                    log.debug("Guardando credenciales");
                    credentialsRepository.save(credentials);

                    log.info("Registro completado exitosamente para el estudiante: {}", studentCode);
                    return new RegisterResponseDTO(true, studentCode,
                            "Registro exitoso. Tu código de estudiante es: " + studentCode);
                } else {
                    log.error("Error en la respuesta de Firebase: {}", response.getStatusCodeValue());
                    return new RegisterResponseDTO(false, null,
                            "Error al crear usuario en el sistema de autenticación");
                }
            } catch (Exception e) {
                log.error("Error al comunicarse con Firebase: {}", e.getMessage(), e);
                return new RegisterResponseDTO(false, null,
                        "Error en el registro con el proveedor de autenticación: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error general en el proceso de registro: {}", e.getMessage(), e);
            return new RegisterResponseDTO(false, null,
                    "Error al registrar: " + e.getMessage());
        }
    }

    /**
     * Genera un correo institucional basado en el código de estudiante
     */
    private String generateInstitutionalEmail(String studentCode) {
        return studentCode.toLowerCase() + "@utp.edu.pe";
    }
}
