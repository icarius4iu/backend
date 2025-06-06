package pe.edu.utp.backend.course.service.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.service.core.CourseService;
import pe.edu.utp.backend.course.service.core.ProfessorService;
import pe.edu.utp.backend.course.service.core.SectionService;
import pe.edu.utp.backend.course.service.core.WeekService;
import pe.edu.utp.backend.util.cicle.Cicle;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CourseManagementServiceTest {

    @Mock
    private CourseService courseService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ProfessorService professorService;

    @Mock
    private WeekService weekService;

    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private CourseManagementService courseManagementService;

    private Course course;
    private CourseDTO courseDTO;
    private CourseCreationRequest courseCreationRequest;
    private Professor professor1;
    private Professor professor2;
    private Section sectionA;
    private Section sectionB;
    private Career career;
    private Cicle cicle;

    @BeforeEach
    void setUp() {
        // Configurar ciclo usando el método de fábrica
        cicle = Cicle.createRegular1(
                "2025",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 7, 15)
        );
        // Establecer el ID para el propósito del test
        cicle.setId(1L);

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
                .sections(new HashSet<>())
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
                .cicleName("2025-1") // El nombre del ciclo regular 1 para 2025
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

        // Configurar profesores
        professor1 = Professor.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@utp.edu.pe")
                .professorCode("P001")
                .sections(new HashSet<>())
                .build();

        professor2 = Professor.builder()
                .id(2L)
                .firstName("María")
                .lastName("Gómez")
                .email("maria.gomez@utp.edu.pe")
                .professorCode("P002")
                .sections(new HashSet<>())
                .build();

        // Configurar secciones
        sectionA = Section.builder()
                .id(1L)
                .code("A")
                .course(course)
                .maxStudents(30)
                .professors(new HashSet<>())
                .weeks(new ArrayList<>())
                .build();

        sectionB = Section.builder()
                .id(2L)
                .code("B")
                .course(course)
                .maxStudents(30)
                .professors(new HashSet<>())
                .weeks(new ArrayList<>())
                .build();

        // Configurar carrera
        career = Career.builder()
                .id(1L)
                .name("Ingeniería de Sistemas")
                .code("SIS")
                .durationSemesters(10)
                .build();
    }

    // Los métodos de test permanecen iguales...
}