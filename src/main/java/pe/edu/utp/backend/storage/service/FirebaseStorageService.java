package pe.edu.utp.backend.storage.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FirebaseStorageService  {

    private final String bucketName;
    private final Resource credentialsResource;
    private Storage storage;

    public FirebaseStorageService(
            @Value("${firebase.storage.bucket}") String bucketName,
            @Value("classpath:static/firebase-adminsdk.json") Resource credentialsResource
    ) {
        this.bucketName = bucketName;
        this.credentialsResource = credentialsResource;
    }

    @PostConstruct
    private void init() {
        try (InputStream serviceAccount = credentialsResource.getInputStream()) {
            this.storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar Firebase Storage", e);
        }
    }

    /**
     * Sube un archivo a una ruta específica dentro del bucket
     * @param file El archivo a subir
     * @param folderPath La ruta donde subir el archivo (terminada en /)
     * @param fileName Nombre que tendrá el archivo (si es null, se genera uno)
     * @return URL pública del archivo
     */
    public String uploadToFolder(MultipartFile file, String folderPath, String fileName) {
        try {
            // Si no se proporciona nombre, generamos uno único
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            }

            // Construimos la ruta completa
            String fullPath = folderPath + fileName;
            InputStream inputStream = file.getInputStream();

            BlobId blobId = BlobId.of(bucketName, fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, inputStream);

            // URL pública: ajusta según permisos de tu bucket
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fullPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo a Firebase Storage en la ruta: " + folderPath, e);
        }
    }

    public void createFolderIfNotExists(String folderPath) {
        // Subir un archivo vacío como .placeholder para crear la carpeta lógica
        String placeholder = folderPath + ".placeholder";
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, placeholder).build();
        storage.create(blobInfo, new byte[0]);
    }

}