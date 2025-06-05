package pe.edu.utp.backend.student.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.repository.StudentRepository;
import pe.edu.utp.backend.student.service.impl.StudentServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentInformationService studentInformationService;

    @InjectMocks
    private StudentServiceImpl studentService;

    // Aquí irán los objetos de prueba
    private Student student;
    private StudentInformation studentInformation;


    // Aquí irán los métodos de test

    @BeforeEach
    void setUp() {
        // Inicializar información del estudiante
        studentInformation = StudentInformation.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .motherLastName("García")
                .documentType(StudentInformation.DocumentType.DNI)
                .documentNumber("72458963")
                .personalEmail("juan@example.com")
                .mobilePhone("987654321")
                .build();

        // Inicializar estudiante
        student = Student.builder()
                .id(1L)
                .studentCode("U20190123")
                .status(Student.Status.ACTIVO)
                .faculty("Ingeniería")
                .modality(Student.Modality.PRESENCIAL)
                .build();

        // Establecer relación bidireccional
        student.setInformation(studentInformation);
        studentInformation.setStudent(student);
    }

    @Test
    void findByStudentCode_shouldReturnStudent_whenStudentExists() {
        // Given
        String studentCode = "U20190123";
        when(studentRepository.findByStudentCode(studentCode)).thenReturn(Optional.of(student));

        // When
        Optional<Student> result = studentService.findByStudentCode(studentCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(studentCode, result.get().getStudentCode());
        assertEquals("Juan", result.get().getInformation().getFirstName());
        verify(studentRepository, times(1)).findByStudentCode(studentCode);
    }
    @Test
    void createStudent_shouldSaveInformationAndStudent() {
        // Given
        when(studentInformationService.save(any(StudentInformation.class))).thenReturn(studentInformation);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        Student result = studentService.createStudent(student, studentInformation);

        // Then
        assertNotNull(result);
        assertEquals("U20190123", result.getStudentCode());
        assertEquals("Juan", result.getInformation().getFirstName());

        // Verificar que se llamaron los métodos necesarios
        verify(studentInformationService, times(1)).save(studentInformation);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void updateStatus_shouldChangeStudentStatus_whenStudentExists() {
        // Given
        Long studentId = 1L;
        Student.Status newStatus = Student.Status.EGRESADO;

        // Estudiante antes de actualizar
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Estudiante después de actualizar
        Student updatedStudent = Student.builder()
                .id(1L)
                .studentCode("U20190123")
                .status(Student.Status.EGRESADO)  // Nuevo estado
                .faculty("Ingeniería")
                .modality(Student.Modality.PRESENCIAL)
                .information(studentInformation)
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // When
        Student result = studentService.updateStatus(studentId, newStatus);

        // Then
        assertNotNull(result);
        assertEquals(Student.Status.EGRESADO, result.getStatus());

        // Verificar que se llamaron los métodos necesarios
        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).save(any(Student.class));
    }
    @Test
    void findByStudentCode_shouldReturnEmpty_whenStudentDoesNotExist() {
        // Given
        String nonExistentCode = "U99999999";
        when(studentRepository.findByStudentCode(nonExistentCode)).thenReturn(Optional.empty());

        // When
        Optional<Student> result = studentService.findByStudentCode(nonExistentCode);

        // Then
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findByStudentCode(nonExistentCode);
    }
}