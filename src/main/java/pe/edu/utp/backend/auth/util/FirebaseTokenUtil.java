package pe.edu.utp.backend.auth.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirebaseTokenUtil {

    public String validateToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            log.debug("Token validado para UID: {}", decodedToken.getUid());
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            log.error("Error validando token: {}", e.getMessage());
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }
    }

    public String getEmailFromToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getEmail();
        } catch (FirebaseAuthException e) {
            log.error("Error obteniendo email del token: {}", e.getMessage());
            throw new RuntimeException("Error procesando token: " + e.getMessage());
        }
    }
}