package pe.edu.utp.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = converter.getObjectMapper();

        // Registrar módulo para manejar proxies de Hibernate
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        mapper.registerModule(hibernateModule);

        // Configuraciones para evitar problemas de serialización
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Para versiones de Jackson < 2.14, usar estas propiedades en lugar de StreamWriteConstraints
        try {
            // Método alternativo para establecer límites en versiones antiguas
            mapper.getSerializationConfig().getDefaultPropertyInclusion();

            // Si estamos en Jackson 2.10+, podemos intentar usar SerializationConfig
            // (Esta propiedad no hace exactamente lo mismo, pero ayuda a evitar ciclos)
            mapper.enable(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID);
        } catch (Exception e) {
            // Ignorar si el método no está disponible
        }

        return converter;
    }
}