package pe.edu.utp.backend.student.service;

import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.model.StudentInformation.DocumentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentInformationService {

    /**
     * Guarda información de estudiante
     */
    StudentInformation save(StudentInformation studentInformation);

    /**
     * Busca información de estudiante por ID
     */
    Optional<StudentInformation> findById(Long id);

    /**
     * Busca información de estudiante por número de documento
     */
    Optional<StudentInformation> findByDocumentNumber(String documentNumber);

    /**
     * Busca información de estudiante por tipo y número de documento
     */
    Optional<StudentInformation> findByDocumentTypeAndNumber(DocumentType type, String number);

    /**
     * Busca información de estudiante por correo electrónico personal
     */
    Optional<StudentInformation> findByPersonalEmail(String email);

    /**
     * Busca estudiantes por nombre completo (búsqueda parcial)
     */
    List<StudentInformation> searchByFullName(String fullNameQuery);

    /**
     * Busca estudiantes por apellido paterno
     */
    List<StudentInformation> findByLastName(String lastName);

    /**
     * Busca estudiantes por departamento
     */
    List<StudentInformation> findByDepartment(String department);

    /**
     * Lista todas las informaciones de estudiantes
     */
    List<StudentInformation> findAll();

    /**
     * Elimina información de estudiante
     */
    void delete(Long id);

    /**
     * Obtiene estadísticas por tipo de documento
     */
    Map<DocumentType, Long> getDocumentTypeStatistics();

    /**
     * Verifica si existe un estudiante con el documento proporcionado
     */
    boolean existsByDocument(String documentNumber);

    /**
     * Verifica si existe un estudiante con el correo proporcionado
     */
    boolean existsByEmail(String email);
}