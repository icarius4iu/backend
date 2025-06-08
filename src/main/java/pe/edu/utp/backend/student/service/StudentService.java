package pe.edu.utp.backend.student.service;

import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.Student.Status;
import pe.edu.utp.backend.student.model.Student.Modality;
import pe.edu.utp.backend.student.model.StudentInformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {

    /**
     * Guarda un estudiante
     */
    Student save(Student student);

    /**
     * Crea un nuevo estudiante con su información
     */
    Student createStudent(Student student, StudentInformation information);

    /**
     * Busca estudiante por ID
     */
    Optional<Student> findById(Long id);

    /**
     * Busca estudiante por código
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Busca estudiante por número de documento
     */
    Optional<Student> findByDocumentNumber(String documentNumber);

    /**
     * Busca estudiantes por estado
     */
    List<Student> findByStatus(Status status);

    /**
     * Busca estudiantes por carrera
     */
    List<Student> findByCareer(Career career);

    /**
     * Busca estudiantes por carrera y estado
     */
    List<Student> findByCareerAndStatus(Career career, Status status);

    /**
     * Busca estudiantes por facultad
     */
    List<Student> findByFaculty(String faculty);

    /**
     * Busca estudiantes por modalidad
     */
    List<Student> findByModality(Modality modality);

    /**
     * Busca estudiantes por campus
     */
    List<Student> findByCampus(String campus);

    /**
     * Busca estudiantes por nombre (búsqueda parcial)
     */
    List<Student> searchByName(String nameQuery);

    /**
     * Lista todos los estudiantes
     */
    List<Student> findAll();

    /**
     * Cambia el estado de un estudiante
     */
    Student updateStatus(Long id, Status newStatus);

    /**
     * Elimina un estudiante
     */
    void delete(Long id);

    /**
     * Obtiene estadísticas por carrera
     */
    Map<String, Long> getCareerStatistics();

    /**
     * Obtiene estadísticas por estado
     */
    Map<Status, Long> getStatusStatistics();

    /**
     * Obtiene estadísticas por modalidad
     */
    Map<Modality, Long> getModalityStatistics();

    /**
     * Verifica si existe un estudiante con el código proporcionado
     */
    boolean existsByStudentCode(String studentCode);

    Optional<Student> findStudentWithSections(Long id);

    List<Student> findStudentsBySection(Long sectionId);
}