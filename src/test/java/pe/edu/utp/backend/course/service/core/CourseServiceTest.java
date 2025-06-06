package pe.edu.utp.backend.course.service.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.course.dto.mapper.CourseMapper;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.exception.InvalidAcademicEntityException;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.validator.CourseValidator;
import pe.edu.utp.backend.util.cicle.Cicle;
import pe.edu.utp.backend.util.cicle.repository.CicleRepository;


import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseValidator courseValidator;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CicleRepository cicleRepository;

    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private Cicle cicle;
    private Career career;
    private CourseCreationRequest courseCreationRequest;
    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        // Configurar ciclo
        cicle = new Cicle();
        cicle.setId(1L);
        cicle.setName("2025-1");
        cicle.setStartDate(LocalDate.of(2025, 3, 15));
        cicle.setEndDate(LocalDate.of(2025, 7, 15));

        // Configurar carrera
        career = Career.builder()
                .id(1L)
                .name("Ingeniería de Sistemas")
                .code("SIS")
                .durationSemesters(10)
                .build();

        // Configurar curso
        course = Course.builder()
                .id(1L)
                .code("SIS101")
                .name("Programación I")
                .description("Curso de introducción a la programación")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicle(cicle)
                .careers(new HashSet<>(Collections.singletonList(career)))
                .build();

        // Configurar request
        courseCreationRequest = CourseCreationRequest.builder()
                .code("SIS101")
                .name("Programación I")
                .description("Curso de introducción a la programación")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(1L)
                .careerIds(Collections.singletonList(1L))
                .build();

        // Configurar courseDTO
        courseDTO = CourseDTO.builder()
                .id(1L)
                .code("SIS101")
                .name("Programación I")
                .description("Curso de introducción a la programación")
                .type("Presencial")
                .credits(4)
                .weeklyHours(6)
                .cicleId(1L)
                .cicleName("2025-1")
                .build();
    }

    @Test
    void testCreateCourse() {
        // Configurar el comportamiento del repositorio
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Ejecutar el método a probar
        Course result = courseService.createCourse(course);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(course.getId(), result.getId());
        assertEquals(course.getCode(), result.getCode());
        assertEquals(course.getName(), result.getName());

        // Verificar que se llamaron los métodos esperados
        verify(courseValidator).validateNewCourse(course);
        verify(courseRepository).save(course);
    }

    @Test
    void testCreateCourseFromRequest() {
        // Configurar comportamiento de los mocks
        when(cicleRepository.findById(1L)).thenReturn(Optional.of(cicle));
        when(careerRepository.findById(1L)).thenReturn(Optional.of(career));
        when(courseMapper.toEntity(eq(courseCreationRequest), any(Cicle.class), anySet()))
                .thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toDTO(any(Course.class))).thenReturn(courseDTO);

        // Ejecutar el método a probar
        CourseDTO result = courseService.createCourseFromRequest(courseCreationRequest);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(courseDTO.getId(), result.getId());
        assertEquals(courseDTO.getCode(), result.getCode());
        assertEquals(courseDTO.getName(), result.getName());

        // Verificar que se llamaron los métodos esperados
        verify(courseValidator).validateNewCourse(any(Course.class));
        verify(courseRepository).save(any(Course.class));
        verify(courseMapper).toDTO(any(Course.class));
    }

    @Test
    void testFindByCode() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findByCode("SIS101")).thenReturn(Optional.of(course));

        // Ejecutar el método a probar
        Course result = courseService.findByCode("SIS101");

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(course.getId(), result.getId());
        assertEquals("SIS101", result.getCode());

        // Verificar que se llamó al repositorio
        verify(courseRepository).findByCode("SIS101");
    }

    @Test
    void testFindByCodeNotFound() {
        // Configurar comportamiento del repositorio para devolver vacío
        when(courseRepository.findByCode("NOTFOUND")).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción esperada
        InvalidAcademicEntityException exception = assertThrows(
                InvalidAcademicEntityException.class,
                () -> courseService.findByCode("NOTFOUND")
        );

        assertEquals("No se encontró un curso con el código: NOTFOUND", exception.getMessage());
        verify(courseRepository).findByCode("NOTFOUND");
    }

    @Test
    void testUpdateCourse() {
        // Configurar comportamiento del repositorio
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Ejecutar el método a probar
        Course result = courseService.updateCourse(course);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(course.getId(), result.getId());

        // Verificar que se llamaron los métodos esperados
        verify(courseValidator).validateExistingCourse(course);
        verify(courseRepository).save(course);
    }

    @Test
    void testFindCoursesByType() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findByType(Course.CourseType.PRESENCIAL))
                .thenReturn(Collections.singletonList(course));

        // Ejecutar el método a probar
        List<Course> results = courseService.findCoursesByType(Course.CourseType.PRESENCIAL);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("SIS101", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(courseRepository).findByType(Course.CourseType.PRESENCIAL);
    }

    @Test
    void testFindCoursesByCicle() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findByCicle(cicle)).thenReturn(Collections.singletonList(course));

        // Ejecutar el método a probar
        List<Course> results = courseService.findCoursesByCicle(cicle);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("SIS101", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(courseRepository).findByCicle(cicle);
    }

    @Test
    void testFindCoursesByCareer() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findByCareersContaining(career)).thenReturn(Collections.singletonList(course));

        // Ejecutar el método a probar
        List<Course> results = courseService.findCoursesByCareer(career);

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("SIS101", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(courseRepository).findByCareersContaining(career);
    }

    @Test
    void testFindCoursesWithAvailableSections() {
        // Configurar comportamiento del repositorio
        when(courseRepository.findCoursesWithAvailableSections()).thenReturn(Collections.singletonList(course));

        // Ejecutar el método a probar
        List<Course> results = courseService.findCoursesWithAvailableSections();

        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("SIS101", results.get(0).getCode());

        // Verificar que se llamó al repositorio
        verify(courseRepository).findCoursesWithAvailableSections();
    }
}