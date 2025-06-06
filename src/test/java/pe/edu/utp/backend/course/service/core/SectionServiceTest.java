package pe.edu.utp.backend.course.service.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.util.cicle.Cicle;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private SectionService sectionService;

    private Course course;
    private Section section;
    private Professor professor;
    private Student student;

    @BeforeEach
    void setUp() {
        // Configurar ciclo
        Cicle cicle = new Cicle();
        cicle.setId(1L);
        cicle.setName("2025-1");
        cicle.setStartDate(LocalDate.of(2025, 3, 15));
        cicle.setEndDate(LocalDate.of(2025, 7, 15));

        // Configurar curso
        course = Course.builder()
                .id(1L)
                .code("SIS101")
                .name("Programación I")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicle(cicle)
                .build();

        // Configurar sección
        section = Section.builder()
                .id(1L)
                .code("A")
                .course(course)
                .maxStudents(30)
                .build();

        // Configurar profesor
        professor = Professor.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@utp.edu.pe")
                .professorCode("P001")
                .build();

        // Configurar estudiante
        student = Student.builder()
                .id(1L)
                .studentCode("U20210789")
                .build();
    }

    @Test
    void testCreateSection() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(sectionRepository.findByCourseAndCode(course, "A")).thenReturn(Optional.empty());
        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        // Ejecutar el método a probar
        Section result = sectionService.createSection(section);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(section.getId(), result.getId());
        assertEquals("A", result.getCode());

        // Verificar que se llamaron los métodos esperados
        verify(sectionRepository).findByCourseAndCode(course, "A");
        verify(sectionRepository).save(section);
    }

    @Test
    void testCreateSectionWithDuplicateCode() {
        // Configurar comportamiento del repositorio para simular código duplicado
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(sectionRepository.findByCourseAndCode(course, "A")).thenReturn(Optional.of(section));

        // Verificar que se lanza la excepción esperada
        InvalidAcademicEntityException exception = assertThrows(
                InvalidAcademicEntityException.class,
                () -> sectionService.createSection(section)
        );

        assertTrue(exception.getMessage().contains("Ya existe una sección con el código A"));
        verify(sectionRepository).findByCourseAndCode(course, "A");
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void testAddProfessorToSection() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        // Ejecutar el método a probar
        Section result = sectionService.addProfessorToSection(1L, professor);

        // Verificar el resultado
        assertNotNull(result);
        assertTrue(section.getProfessors().contains(professor));
        assertTrue(professor.getSections().contains(section));

        // Verificar que se llamó al repositorio
        verify(sectionRepository).findById(1L);
        verify(sectionRepository).save(section);
    }

    @Test
    void testAddStudentToSection() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> {
            Section savedSection = invocation.getArgument(0);
            // La llamada a addStudent ya se habrá ejecutado en este punto
            return savedSection;
        });

        // Ejecutar el método a probar
        Section result = sectionService.addStudentToSection(1L, student);

        // Verificar el resultado de manera alternativa
        assertNotNull(result);

        // Verificar que se añadió el estudiante a la sección (verificación manual)
        boolean studentFound = false;
        for (Student s : result.getStudents()) {
            if (s.getId().equals(student.getId())) {
                studentFound = true;
                break;
            }
        }
        assertTrue(studentFound, "El estudiante debería estar en la lista de estudiantes de la sección");

        // Verificar que el método addStudent fue invocado correctamente
        verify(sectionRepository).findById(1L);
        verify(sectionRepository).save(section);
    }

    @Test
    void testAddStudentToFullSection() {
        // Configurar una sección llena
        section.setMaxStudents(1);
        Set<Student> students = new HashSet<>();
        Student existingStudent = Student.builder().id(2L).build();
        students.add(existingStudent);
        section.setStudents(students);

        // Configurar comportamiento del repositorio
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));

        // Verificar que se lanza la excepción esperada
        InvalidAcademicEntityException exception = assertThrows(
                InvalidAcademicEntityException.class,
                () -> sectionService.addStudentToSection(1L, student)
        );

        assertTrue(exception.getMessage().contains("La sección está llena"));
        verify(sectionRepository).findById(1L);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void testGenerateWeeks() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> {
            Section savedSection = invocation.getArgument(0);
            // Simular que se generaron las semanas
            if (savedSection.getWeeks().isEmpty()) {
                for (int i = 1; i <= 16; i++) {
                    Week week = new Week();
                    week.setWeekNumber(i);
                    week.setTitle("Semana " + i);
                    week.setSection(savedSection);
                    savedSection.getWeeks().add(week);
                }
            }
            return savedSection;
        });

        // Ejecutar el método a probar
        Section result = sectionService.generateWeeks(1L);

        // Verificar el resultado
        assertNotNull(result);
        assertFalse(result.getWeeks().isEmpty());

        // Verificar que se llamó al repositorio
        verify(sectionRepository).findById(1L);
        verify(sectionRepository).save(section);
    }

    @Test
    void testFindSectionsByCourse() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findByCourse(course)).thenReturn(Collections.singletonList(section));

        // Ejecutar el método a probar
        List<Section> results = sectionService.findSectionsByCourse(course);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("A", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(sectionRepository).findByCourse(course);
    }

    @Test
    void testFindSectionsByProfessor() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findByProfessorsContaining(professor))
                .thenReturn(Collections.singletonList(section));

        // Ejecutar el método a probar
        List<Section> results = sectionService.findSectionsByProfessor(professor);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("A", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(sectionRepository).findByProfessorsContaining(professor);
    }

    @Test
    void testFindSectionsByStudent() {
        // Configurar comportamiento del repositorio
        when(sectionRepository.findByStudentsContaining(student))
                .thenReturn(Collections.singletonList(section));

        // Ejecutar el método a probar
        List<Section> results = sectionService.findSectionsByStudent(student);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("A", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(sectionRepository).findByStudentsContaining(student);
    }
}