package pe.edu.utp.backend.auth.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StopWatch;
// Importar las clases generadas por el proto
import pe.edu.utp.backend.auth.grpc.AuthServiceGrpc;
import pe.edu.utp.backend.auth.grpc.LoginRequest;
import pe.edu.utp.backend.auth.grpc.LoginResponse;
import pe.edu.utp.backend.auth.grpc.VerifyTokenRequest;
import pe.edu.utp.backend.auth.grpc.VerifyTokenResponse;
// Importar tus clases existentes
import pe.edu.utp.backend.auth.dtos.AuthRequestDTO;
import pe.edu.utp.backend.auth.dtos.AuthResponseDTO;
import pe.edu.utp.backend.auth.dtos.VerifyTokenResponseDTO;
import pe.edu.utp.backend.auth.service.FirebaseAuthService;

import java.util.concurrent.TimeUnit;

/**
 * Servicio gRPC para autenticación de estudiantes
 */
@GrpcService
@Slf4j
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final FirebaseAuthService authService;
    private final MeterRegistry meterRegistry;

    // Métricas para monitoreo
    private final Timer loginTimer;
    private final Timer verifyTokenTimer;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter verifyTokenSuccessCounter;
    private final Counter verifyTokenFailureCounter;

    public AuthGrpcService(FirebaseAuthService authService, MeterRegistry meterRegistry) {
        this.authService = authService;
        this.meterRegistry = meterRegistry;

        // Inicializar métricas
        this.loginTimer = Timer.builder("grpc.auth.login.duration")
                .description("Tiempo de respuesta para login gRPC")
                .register(meterRegistry);

        this.verifyTokenTimer = Timer.builder("grpc.auth.verify_token.duration")
                .description("Tiempo de respuesta para verificación de token gRPC")
                .register(meterRegistry);

        this.loginSuccessCounter = Counter.builder("grpc.auth.login.success")
                .description("Número de logins exitosos")
                .register(meterRegistry);

        this.loginFailureCounter = Counter.builder("grpc.auth.login.failure")
                .description("Número de logins fallidos")
                .register(meterRegistry);

        this.verifyTokenSuccessCounter = Counter.builder("grpc.auth.verify_token.success")
                .description("Número de verificaciones de token exitosas")
                .register(meterRegistry);

        this.verifyTokenFailureCounter = Counter.builder("grpc.auth.verify_token.failure")
                .description("Número de verificaciones de token fallidas")
                .register(meterRegistry);
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        StopWatch watch = new StopWatch();
        watch.start();

        String studentCode = request.getStudentCode();
        log.info("Recibida solicitud de login gRPC para código: '{}'", studentCode);

        try {
            // Validación básica
            if (studentCode == null || studentCode.isEmpty()) {
                handleError(responseObserver, Status.INVALID_ARGUMENT, "Código de estudiante no proporcionado");
                loginFailureCounter.increment();
                return;
            }

            // Convertir el request de gRPC a DTO
            AuthRequestDTO dto = new AuthRequestDTO(
                    studentCode,
                    request.getPassword()
            );

            // Llamar al servicio de autenticación existente
            var result = authService.authenticate(dto);

            // Construir respuesta gRPC
            LoginResponse.Builder responseBuilder = LoginResponse.newBuilder()
                    .setSuccess(result.isSuccess());

            if (result.isSuccess()) {
                // Asignar valores con protección contra nulos
                responseBuilder
                        .setToken(result.getToken() != null ? result.getToken() : "")
                        .setEmail(result.getEmail() != null ? result.getEmail() : "")
                        .setUid(result.getUid() != null ? result.getUid() : "")
                        .setName(result.getStudentName() != null ? result.getStudentName() : "")
                        .setStudentCode(result.getStudentCode() != null ? result.getStudentCode() : "")
                        .setCareer(result.getCareerName() != null ? result.getCareerName() : "");

                loginSuccessCounter.increment();
                log.info("Login exitoso para estudiante: {}", studentCode);
            } else {
                responseBuilder.setErrorMessage(result.getErrorMessage() != null ? result.getErrorMessage() : "Error desconocido");
                loginFailureCounter.increment();
                log.info("Login fallido para estudiante: {}, motivo: {}", studentCode, result.getErrorMessage());
            }

            // Enviar respuesta
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            loginFailureCounter.increment();
            log.error("Error inesperado en login gRPC para código: {}", studentCode, e);
            handleError(responseObserver, Status.INTERNAL, "Error interno del servidor");
        } finally {
            watch.stop();
            loginTimer.record(watch.getTotalTimeMillis(), TimeUnit.MILLISECONDS);
            log.debug("Tiempo de procesamiento login gRPC: {} ms", watch.getTotalTimeMillis());
        }
    }

    @Override
    public void verifyToken(VerifyTokenRequest request, StreamObserver<VerifyTokenResponse> responseObserver) {
        StopWatch watch = new StopWatch();
        watch.start();

        log.info("Recibida solicitud de verificación de token gRPC");

        try {
            String token = request.getToken();

            // Validación básica
            if (token == null || token.isEmpty()) {
                handleTokenError(responseObserver, Status.INVALID_ARGUMENT, "Token no proporcionado");
                verifyTokenFailureCounter.increment();
                return;
            }

            // Verificar token
            VerifyTokenResponseDTO result = authService.verifyToken(token);

            // Construir y enviar respuesta
            VerifyTokenResponse.Builder responseBuilder = VerifyTokenResponse.newBuilder()
                    .setSuccess(result.isSuccess())
                    .setMessage(result.getMessage() != null ? result.getMessage() : "")
                    .setUserId(result.isSuccess() && result.getUserId() != null ? result.getUserId() : "");

            VerifyTokenResponse response = responseBuilder.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            // Registrar métricas y logs
            if (result.isSuccess()) {
                verifyTokenSuccessCounter.increment();
                log.info("Verificación de token exitosa para usuario: {}", result.getUserId());
            } else {
                verifyTokenFailureCounter.increment();
                log.info("Verificación de token fallida: {}", result.getMessage());
            }

        } catch (Exception e) {
            verifyTokenFailureCounter.increment();
            log.error("Error inesperado verificando token", e);
            handleTokenError(responseObserver, Status.INTERNAL, "Error interno al verificar token");
        } finally {
            watch.stop();
            verifyTokenTimer.record(watch.getTotalTimeMillis(), TimeUnit.MILLISECONDS);
            log.debug("Tiempo de procesamiento verificación token gRPC: {} ms", watch.getTotalTimeMillis());
        }
    }

    /**
     * Maneja errores en el método login
     */
    private void handleError(StreamObserver<LoginResponse> responseObserver, Status status, String message) {
        LoginResponse response = LoginResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Maneja errores en el método verifyToken
     */
    private void handleTokenError(StreamObserver<VerifyTokenResponse> responseObserver, Status status, String message) {
        VerifyTokenResponse response = VerifyTokenResponse.newBuilder()
                .setSuccess(false)
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}