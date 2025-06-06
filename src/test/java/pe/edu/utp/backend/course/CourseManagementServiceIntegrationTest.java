package pe.edu.utp.backend.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.course.dto.request.CourseCreationRequest;
import pe.edu.utp.backend.course.dto.response.CourseDTO;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Professor;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.course.model.Week;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.course.repository.ProfessorRepository;
import pe.edu.utp.backend.course.repository.SectionRepository;
import pe.edu.utp.backend.course.service.composite.CourseManagementService;
import pe.edu.utp.backend.course.service.core.CourseService;
import pe.edu.utp.backend.course.service.core.ProfessorService;
import pe.edu.utp.backend.course.service.core.SectionService;
import pe.edu.utp.backend.util.cicle.Cicle;
import pe.edu.utp.backend.util.cicle.repository.CicleRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseManagementServiceIntegrationTest {

    @Autowired
    private CourseManagementService courseManagementService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CicleRepository cicleRepository;

    private Cicle cicle;
    private Career career1;
    private Career career2;
    private Professor professor1;
    private Professor professor2;
    private CourseCreationRequest courseRequest;

    // Contador estático para asegurar unicidad incluso si los tests se ejecutan muy rápido
    private static int codeCounter = 1;

    /**
     * Método para generar códigos de curso absolutamente únicos
     */
    private String generateUniqueCode(String baseCode) {
        // Usar una combinación de timestamp, UUID truncado y contador
        String uniquePart = String.format("%d%03d",
                System.currentTimeMillis() % 10000,  // Últimos 4 dígitos del timestamp
                codeCounter++);                      // Contador secuencial

        return baseCode + uniquePart;
    }

    @BeforeEach
    void setUp() {
        // Crear y guardar un ciclo académico
        cicle = Cicle.createRegular1(
                "2025",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 7, 15)
        );
        cicle = cicleRepository.save(cicle);

        // Crear y guardar carreras
        career1 = Career.builder()
                .name("Ingeniería de Sistemas")
                .code("SIS")
                .durationSemesters(10)
                .build();
        career1 = careerRepository.save(career1);

        career2 = Career.builder()
                .name("Ingeniería de Software")
                .code("SOFT")
                .durationSemesters(10)
                .build();
        career2 = careerRepository.save(career2);

        // Crear y guardar profesores
        professor1 = Professor.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@utp.edu.pe")
                .professorCode("P001")
                .department("Computación")
                .specialization("Programación")
                .build();
        professor1 = professorRepository.save(professor1);

        professor2 = Professor.builder()
                .firstName("María")
                .lastName("Gómez")
                .email("maria.gomez@utp.edu.pe")
                .professorCode("P002")
                .department("Computación")
                .specialization("Base de Datos")
                .build();
        professor2 = professorRepository.save(professor2);

        // Preparar solicitud de creación de curso con código único
        courseRequest = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Programación I")
                .description("Curso de introducción a la programación")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(cicle.getId())
                .careerIds(Collections.singletonList(career1.getId()))
                .build();
    }

    @Test
    void testCreateCourseWithSections() {
        // Ejecutar método a probar
        Course createdCourse = courseManagementService.createCourseWithSections(
                courseRequest, 2, 30, Arrays.asList(professor1.getId(), professor2.getId()));

        // Verificar que el curso se creó correctamente
        assertNotNull(createdCourse);
        assertEquals(courseRequest.getCode(), createdCourse.getCode());
        assertEquals("Programación I", createdCourse.getName());
        assertEquals(Course.CourseType.PRESENCIAL, createdCourse.getType());

        // Verificar que se crearon las secciones
        List<Section> sections = sectionRepository.findByCourse(createdCourse);
        assertEquals(2, sections.size());

        // Verificar que las secciones tienen códigos A y B
        boolean hasA = false;
        boolean hasB = false;
        for (Section section : sections) {
            if ("A".equals(section.getCode())) hasA = true;
            if ("B".equals(section.getCode())) hasB = true;

            // Verificar capacidad de estudiantes
            assertEquals(30, section.getMaxStudents());

            // Verificar que tienen semanas generadas
            assertFalse(section.getWeeks().isEmpty());
            assertEquals(cicle.getWeeksCount(), section.getWeeks().size());

            // Verificar que tienen profesores asignados
            assertFalse(section.getProfessors().isEmpty());
        }

        assertTrue(hasA, "Debe existir una sección con código A");
        assertTrue(hasB, "Debe existir una sección con código B");

        // Verificar que el curso está asociado a la carrera
        assertTrue(createdCourse.getCareers().contains(career1),
                "El curso debe estar asociado a la carrera de Ingeniería de Sistemas");
    }

    @Test
    void testAssignProfessorsToSections() {
        // Crear un curso con secciones pero sin profesores
        // Usamos un código diferente para este test
        CourseCreationRequest requestForTest = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Programación I - Test Profesores")
                .description("Curso de introducción a la programación")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(cicle.getId())
                .careerIds(Collections.singletonList(career1.getId()))
                .build();

        // Crear el curso con secciones pero sin profesores inicialmente
        Course course = courseManagementService.createCourseWithSections(
                requestForTest, 2, 30, Collections.emptyList());

        // Verificar que el curso tiene secciones
        assertFalse(course.getSections().isEmpty());
        assertEquals(2, course.getSections().size());

        // Ejecutar método a probar - asignar profesores a las secciones
        Course updatedCourse = courseManagementService.assignProfessorsToSections(
                course, Arrays.asList(professor1.getId(), professor2.getId()));

        // Verificar que los profesores fueron asignados
        for (Section section : updatedCourse.getSections()) {
            assertFalse(section.getProfessors().isEmpty(),
                    "Cada sección debe tener al menos un profesor asignado");
        }

        // Verificar que ambos profesores fueron asignados
        List<Professor> allProfessors = professorRepository.findAll();
        boolean prof1Assigned = false;
        boolean prof2Assigned = false;

        for (Professor prof : allProfessors) {
            if (prof.getId().equals(professor1.getId()) && !prof.getSections().isEmpty()) {
                prof1Assigned = true;
            }
            if (prof.getId().equals(professor2.getId()) && !prof.getSections().isEmpty()) {
                prof2Assigned = true;
            }
        }

        assertTrue(prof1Assigned, "El profesor 1 debe estar asignado a alguna sección");
        assertTrue(prof2Assigned, "El profesor 2 debe estar asignado a alguna sección");
    }

    @Test
    void testAddCourseToCarreras() {
        // Crear un curso con una carrera inicial
        // Usamos un código diferente para este test
        CourseCreationRequest requestWithOneCareer = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Programación II")
                .description("Curso avanzado de programación")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(cicle.getId())
                .careerIds(Collections.singletonList(career1.getId()))  // Asociamos al menos una carrera
                .build();

        CourseDTO courseDTO = courseService.createCourseFromRequest(requestWithOneCareer);
        Course course = courseService.getById(courseDTO.getId());

        // Verificar que inicialmente solo está asociado a una carrera
        assertEquals(1, course.getCareers().size());
        assertTrue(course.getCareers().contains(career1));

        // Ejecutar método a probar - añadimos la segunda carrera
        Course updatedCourse = courseManagementService.addCourseToCarreras(
                course.getId(), Collections.singletonList(career2.getId()));

        // Verificar que el curso ahora está asociado a ambas carreras
        assertEquals(2, updatedCourse.getCareers().size());

        boolean hasCareer1 = false;
        boolean hasCareer2 = false;

        for (Career c : updatedCourse.getCareers()) {
            if (c.getId().equals(career1.getId())) hasCareer1 = true;
            if (c.getId().equals(career2.getId())) hasCareer2 = true;
        }

        assertTrue(hasCareer1, "El curso debe mantener la asociación con Ingeniería de Sistemas");
        assertTrue(hasCareer2, "El curso debe estar asociado a la nueva carrera de Ingeniería de Software");
    }

    @Test
    void testUpdateWeekDates() {
        // Crear un curso con secciones y semanas
        // Usamos un código diferente para este test
        CourseCreationRequest requestForTest = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Programación I - Test Semanas")
                .description("Curso para prueba de fechas de semanas")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(cicle.getId())
                .careerIds(Collections.singletonList(career1.getId()))
                .build();

        Course course = courseManagementService.createCourseWithSections(
                requestForTest, 1, 30, Collections.singletonList(professor1.getId()));

        // Ejecutar método a probar
        Course updatedCourse = courseManagementService.updateWeekDates(course.getId());

        // Verificar que las semanas tienen fechas asignadas
        for (Section section : updatedCourse.getSections()) {
            for (Week week : section.getWeeks()) {
                assertNotNull(week.getStartDate(), "La fecha de inicio no debe ser nula");
                assertNotNull(week.getEndDate(), "La fecha de fin no debe ser nula");
                assertTrue(week.getStartDate().isBefore(week.getEndDate()),
                        "La fecha de inicio debe ser anterior a la fecha de fin");

                // Verificar que las fechas están dentro del rango del ciclo
                assertFalse(week.getStartDate().isBefore(cicle.getStartDate()),
                        "La fecha de inicio de la semana no puede ser anterior a la fecha de inicio del ciclo");
                assertFalse(week.getEndDate().isAfter(cicle.getEndDate()),
                        "La fecha de fin de la semana no puede ser posterior a la fecha de fin del ciclo");
            }
        }
    }

    @Test
    void testCourseWithMultipleCarreras() {
        // Crear un curso asociado a múltiples carreras desde el inicio
        // Usamos un código diferente para este test
        CourseCreationRequest multiCareerRequest = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Estructura de Datos")
                .description("Curso de estructuras de datos y algoritmos")
                .type(Course.CourseType.PRESENCIAL)
                .credits(4)
                .weeklyHours(6)
                .cicleId(cicle.getId())
                .careerIds(Arrays.asList(career1.getId(), career2.getId()))  // Múltiples carreras
                .build();

        CourseDTO courseDTO = courseService.createCourseFromRequest(multiCareerRequest);
        Course course = courseService.getById(courseDTO.getId());

        // Verificar que el curso está asociado a ambas carreras
        assertEquals(2, course.getCareers().size());

        boolean hasCareer1 = false;
        boolean hasCareer2 = false;

        for (Career c : course.getCareers()) {
            if (c.getId().equals(career1.getId())) hasCareer1 = true;
            if (c.getId().equals(career2.getId())) hasCareer2 = true;
        }

        assertTrue(hasCareer1, "El curso debe estar asociado a Ingeniería de Sistemas");
        assertTrue(hasCareer2, "El curso debe estar asociado a Ingeniería de Software");

        // Verificar que podemos recuperar todas las carreras del curso
        Course retrievedCourse = courseService.getById(course.getId());
        assertEquals(2, retrievedCourse.getCareers().size(),
                "El curso recuperado debe tener 2 carreras asociadas");
    }

    @Test
    void testCreateAndAssignToDifferentCareers() {
        // Creamos un curso con una carrera y luego verificamos que podemos
        // asignar a otras carreras y recuperarlo correctamente

        // Crear un curso con una sola carrera
        CourseCreationRequest singleCareerRequest = CourseCreationRequest.builder()
                .code(generateUniqueCode("SIS"))
                .name("Algoritmos")
                .description("Curso de algoritmos y complejidad")
                .type(Course.CourseType.PRESENCIAL)
                .credits(3)
                .weeklyHours(4)
                .cicleId(cicle.getId())
                .careerIds(Collections.singletonList(career1.getId()))
                .build();

        CourseDTO courseDTO = courseService.createCourseFromRequest(singleCareerRequest);
        Course course = courseService.getById(courseDTO.getId());

        // Verificar la asociación inicial
        assertEquals(1, course.getCareers().size());
        assertTrue(course.getCareers().contains(career1));

        // Crear una tercera carrera
        Career career3 = Career.builder()
                .name("Ingeniería de Datos")
                .code("DATA")
                .durationSemesters(10)
                .build();
        career3 = careerRepository.save(career3);

        // Asignar el curso a la tercera carrera
        Course updatedCourse = courseManagementService.addCourseToCarreras(
                course.getId(), Collections.singletonList(career3.getId()));

        // Verificar que ahora tiene 2 carreras
        assertEquals(2, updatedCourse.getCareers().size());

        // Verificar que podemos recuperar todas las carreras
        Course retrievedCourse = courseService.getById(course.getId());
        assertEquals(2, retrievedCourse.getCareers().size());

        // Verificar que son las carreras correctas
        boolean hasCareer1 = false;
        boolean hasCareer3 = false;

        for (Career c : retrievedCourse.getCareers()) {
            if (c.getId().equals(career1.getId())) hasCareer1 = true;
            if (c.getId().equals(career3.getId())) hasCareer3 = true;
        }

        assertTrue(hasCareer1, "El curso debe mantener su asociación con la carrera original");
        assertTrue(hasCareer3, "El curso debe estar asociado a la nueva carrera");
    }
}