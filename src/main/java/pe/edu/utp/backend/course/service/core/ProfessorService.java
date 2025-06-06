package pe.edu.utp.backend.course.service.core;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.repository.ProfessorRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService extends BaseAcademicService<Professor, Long> {

    public ProfessorService(ProfessorRepository professorRepository) {
        super(professorRepository);
    }

    public ProfessorRepository getProfessorRepository() {
        return (ProfessorRepository) repository;
    }

    @Transactional(readOnly = true)
    public Optional<Professor> findByEmail(String email) {
        return getProfessorRepository().findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Professor> findByProfessorCode(String code) {
        return getProfessorRepository().findByProfessorCode(code);
    }

    @Transactional(readOnly = true)
    public List<Professor> findByDepartment(String department) {
        return getProfessorRepository().findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Professor> findBySpecialization(String specialization) {
        return getProfessorRepository().findBySpecialization(specialization);
    }

    @Transactional(readOnly = true)
    public List<Professor> findBySectionsContaining(Section section) {
        return getProfessorRepository().findBySectionsContaining(section);
    }

    @Transactional(readOnly = true)
    public List<Professor> findByCourse(Course course) {
        return getProfessorRepository().findProfessorsByCourse(course);
    }

    @Transactional
    public Professor createProfessor(Professor professor) {
        validateNewProfessor(professor);
        return save(professor);
    }

    @Transactional
    public Professor updateProfessor(Professor professor) {
        validateExistingProfessor(professor);
        return save(professor);
    }

    @Transactional
    public Professor assignToSection(Long professorId, Long sectionId) {
        // Este método sería mejor implementarlo en un servicio compuesto
        // ya que implica la interacción entre dos entidades diferentes
        throw new UnsupportedOperationException("Esta operación debe realizarse a través del SectionService");
    }

    /**
     * Valida que un nuevo profesor sea válido
     */
    private void validateNewProfessor(Professor professor) {
        if (professor == null) {
            throw new InvalidAcademicEntityException("El profesor no puede ser nulo");
        }

        if (professor.getFirstName() == null || professor.getFirstName().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El nombre del profesor es obligatorio");
        }

        if (professor.getLastName() == null || professor.getLastName().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El apellido del profesor es obligatorio");
        }

        if (professor.getEmail() == null || professor.getEmail().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El email del profesor es obligatorio");
        }

        if (professor.getProfessorCode() == null || professor.getProfessorCode().trim().isEmpty()) {
            throw new InvalidAcademicEntityException("El código del profesor es obligatorio");
        }

        // Verificar si ya existe un profesor con el mismo código
        if (getProfessorRepository().existsByProfessorCode(professor.getProfessorCode())) {
            throw new InvalidAcademicEntityException(
                    "Ya existe un profesor con el código " + professor.getProfessorCode());
        }

        // Verificar si ya existe un profesor con el mismo email
        Optional<Professor> existingByEmail = getProfessorRepository().findByEmail(professor.getEmail());
        if (existingByEmail.isPresent()) {
            throw new InvalidAcademicEntityException(
                    "Ya existe un profesor con el email " + professor.getEmail());
        }
    }

    /**
     * Valida que un profesor existente para actualización sea válido
     */
    private void validateExistingProfessor(Professor professor) {
        if (professor == null) {
            throw new InvalidAcademicEntityException("El profesor no puede ser nulo");
        }

        if (professor.getId() == null) {
            throw new InvalidAcademicEntityException("El ID del profesor es obligatorio para actualización");
        }

        // Las mismas validaciones que para un nuevo profesor pero sin validar
        // duplicados o implementando lógica que excluya al propio profesor
    }
}