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
import pe.edu.utp.backend.course.repository.ProfessorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private ProfessorService professorService;

    private Professor professor;
    private Course course;
    private Section section;

    @BeforeEach
    void setUp() {
        // Configurar profesor
        professor = Professor.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@utp.edu.pe")
                .professorCode("P001")
                .department("Computación")
                .specialization("Programación")
                .build();

        // Configurar curso
        course = Course.builder()
                .id(1L)
                .code("SIS101")
                .name("Programación I")
                .build();

        // Configurar sección
        section = Section.builder()
                .id(1L)
                .code("A")
                .course(course)
                .build();
    }

    @Test
    void testCreateProfessor() {
        // Configurar comportamiento del repositorio
        when(professorRepository.existsByProfessorCode(professor.getProfessorCode())).thenReturn(false);
        when(professorRepository.findByEmail(professor.getEmail())).thenReturn(Optional.empty());
        when(professorRepository.save(any(Professor.class))).thenReturn(professor);

        // Ejecutar el método a probar
        Professor result = professorService.createProfessor(professor);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(professor.getId(), result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Pérez", result.getLastName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).existsByProfessorCode(professor.getProfessorCode());
        verify(professorRepository).findByEmail(professor.getEmail());
        verify(professorRepository).save(professor);
    }

    @Test
    void testCreateProfessorWithExistingCode() {
        // Configurar comportamiento del repositorio para simular código duplicado
        when(professorRepository.existsByProfessorCode(professor.getProfessorCode())).thenReturn(true);

        // Verificar que se lanza la excepción esperada
        InvalidAcademicEntityException exception = assertThrows(
                InvalidAcademicEntityException.class,
                () -> professorService.createProfessor(professor)
        );

        assertTrue(exception.getMessage().contains("Ya existe un profesor con el código"));
        verify(professorRepository).existsByProfessorCode(professor.getProfessorCode());
        verifyNoMoreInteractions(professorRepository);
    }

    @Test
    void testCreateProfessorWithExistingEmail() {
        // Configurar comportamiento del repositorio
        when(professorRepository.existsByProfessorCode(professor.getProfessorCode())).thenReturn(false);
        when(professorRepository.findByEmail(professor.getEmail())).thenReturn(Optional.of(professor));

        // Verificar que se lanza la excepción esperada
        InvalidAcademicEntityException exception = assertThrows(
                InvalidAcademicEntityException.class,
                () -> professorService.createProfessor(professor)
        );

        assertTrue(exception.getMessage().contains("Ya existe un profesor con el email"));
        verify(professorRepository).existsByProfessorCode(professor.getProfessorCode());
        verify(professorRepository).findByEmail(professor.getEmail());
        verifyNoMoreInteractions(professorRepository);
    }

    @Test
    void testFindByEmail() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findByEmail("juan.perez@utp.edu.pe")).thenReturn(Optional.of(professor));

        // Ejecutar el método a probar
        Optional<Professor> result = professorService.findByEmail("juan.perez@utp.edu.pe");

        // Verificar el resultado
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findByEmail("juan.perez@utp.edu.pe");
    }

    @Test
    void testFindByProfessorCode() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findByProfessorCode("P001")).thenReturn(Optional.of(professor));

        // Ejecutar el método a probar
        Optional<Professor> result = professorService.findByProfessorCode("P001");

        // Verificar el resultado
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findByProfessorCode("P001");
    }

    @Test
    void testFindByDepartment() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findByDepartment("Computación"))
                .thenReturn(Collections.singletonList(professor));

        // Ejecutar el método a probar
        List<Professor> results = professorService.findByDepartment("Computación");

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Juan", results.get(0).getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findByDepartment("Computación");
    }

    @Test
    void testFindBySpecialization() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findBySpecialization("Programación"))
                .thenReturn(Collections.singletonList(professor));

        // Ejecutar el método a probar
        List<Professor> results = professorService.findBySpecialization("Programación");

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Juan", results.get(0).getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findBySpecialization("Programación");
    }

    @Test
    void testFindBySectionsContaining() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findBySectionsContaining(section))
                .thenReturn(Collections.singletonList(professor));

        // Ejecutar el método a probar
        List<Professor> results = professorService.findBySectionsContaining(section);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Juan", results.get(0).getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findBySectionsContaining(section);
    }

    @Test
    void testFindByCourse() {
        // Configurar comportamiento del repositorio
        when(professorRepository.findProfessorsByCourse(course))
                .thenReturn(Collections.singletonList(professor));

        // Ejecutar el método a probar
        List<Professor> results = professorService.findByCourse(course);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Juan", results.get(0).getFirstName());

        // Verificar que se llamó al repositorio
        verify(professorRepository).findProfessorsByCourse(course);
    }

    @Test
    void testUpdateProfessor() {
        // Configurar profesor con ID para actualización
        professor.setId(1L);

        // Configurar comportamiento del repositorio
        when(professorRepository.save(any(Professor.class))).thenReturn(professor);

        // Ejecutar el método a probar
        Professor result = professorService.updateProfessor(professor);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(professor.getId(), result.getId());

        // Verificar que se llamó al repositorio
        verify(professorRepository).save(professor);
    }
}