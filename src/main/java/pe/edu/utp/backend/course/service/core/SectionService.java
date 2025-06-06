package pe.edu.utp.backend.course.service.core;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.course.service.base.BaseAcademicService;
import pe.edu.utp.backend.student.model.Student;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService extends BaseAcademicService<Section, Long> {

    private final CourseRepository courseRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository) {
        super(sectionRepository);
        this.courseRepository = courseRepository;
    }

    public SectionRepository getSectionRepository() {
        return (SectionRepository) repository;
    }

    @Transactional
    public Section createSection(Section section) {
        // Validar que la sección tenga un curso asignado
        if (section.getCourse() == null) {
            throw new InvalidAcademicEntityException("La sección debe tener un curso asignado");
        }

        // Validar que el curso exista
        if (section.getCourse().getId() != null) {
            courseRepository.findById(section.getCourse().getId())
                    .orElseThrow(() -> new InvalidAcademicEntityException(
                            "No se encontró el curso con ID: " + section.getCourse().getId()));
        }

        // Validar que el código de la sección no esté duplicado para el mismo curso
        Optional<Section> existingSection = getSectionRepository().findByCourseAndCode(
                section.getCourse(), section.getCode());

        if (existingSection.isPresent()) {
            throw new InvalidAcademicEntityException(
                    "Ya existe una sección con el código " + section.getCode() +
                            " para el curso " + section.getCourse().getName());
        }

        return save(section);
    }

    @Transactional
    public Section addProfessorToSection(Long sectionId, Professor professor) {
        Section section = getById(sectionId);
        section.addProfessor(professor);
        return save(section);
    }

    @Transactional
    public Section addStudentToSection(Long sectionId, Student student) {
        Section section = getById(sectionId);

        // Verificar si hay cupo disponible
        if (section.isFull()) {
            throw new InvalidAcademicEntityException(
                    "La sección está llena, no se pueden agregar más estudiantes");
        }

        section.addStudent(student);
        return save(section);
    }

    @Transactional
    public Section generateWeeks(Long sectionId) {
        Section section = getById(sectionId);
        section.generateWeeks();
        return save(section);
    }

    public List<Section> findSectionsByCourse(Course course) {
        return getSectionRepository().findByCourse(course);
    }

    public List<Section> findSectionsByProfessor(Professor professor) {
        return getSectionRepository().findByProfessorsContaining(professor);
    }

    public List<Section> findSectionsByStudent(Student student) {
        return getSectionRepository().findByStudentsContaining(student);
    }

    public List<Section> findSectionsWithAvailableSeats() {
        return getSectionRepository().findSectionsWithAvailableSeats();
    }
}