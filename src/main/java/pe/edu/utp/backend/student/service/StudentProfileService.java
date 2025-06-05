package pe.edu.utp.backend.student.service;

import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.StudentProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentProfileService {

    /**
     * Guarda un perfil de estudiante
     */
    StudentProfile save(StudentProfile studentProfile);

    /**
     * Crea o actualiza un perfil basado en un estudiante
     */
    StudentProfile createOrUpdateProfile(Student student);

    /**
     * Busca perfil por ID
     */
    Optional<StudentProfile> findById(Long id);

    /**
     * Busca perfil por ID de estudiante
     */
    Optional<StudentProfile> findByStudentId(Long studentId);

    /**
     * Busca perfil por código de estudiante
     */
    Optional<StudentProfile> findByStudentCode(String studentCode);

    /**
     * Busca perfil por número de documento
     */
    Optional<StudentProfile> findByDocumentNumber(String documentNumber);

    /**
     * Busca perfiles por nombre (búsqueda parcial)
     */
    List<StudentProfile> searchByName(String nameQuery);

    /**
     * Busca perfiles por facultad
     */
    List<StudentProfile> findByFaculty(String faculty);

    /**
     * Lista todos los perfiles
     */
    List<StudentProfile> findAll();

    /**
     * Actualiza la foto de perfil de un estudiante
     */
    StudentProfile updateProfilePhoto(Long id, String photoUrl);

    /**
     * Busca perfiles actualizados después de cierta fecha
     */
    List<StudentProfile> findUpdatedAfter(LocalDateTime date);

    /**
     * Encuentra perfiles que tienen foto
     */
    List<StudentProfile> findAllWithPhotos();

    /**
     * Elimina un perfil
     */
    void delete(Long id);

    /**
     * Sincroniza todos los perfiles con la información actual de los estudiantes
     */
    void syncAllProfiles();

    /**
     * Obtiene estadísticas por facultad
     */
    Map<String, Long> getFacultyStatistics();

    /**
     * Verifica si existe un perfil para el estudiante
     */
    boolean existsByStudentId(Long studentId);
}