package pe.edu.utp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Seguro para APIs con autenticación basada en tokens
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()  // Endpoints REST públicos por ahora
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}