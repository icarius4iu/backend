package pe.edu.utp.backend.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.util.career.repository.CareerRepository;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.model.StudentProfile;
import pe.edu.utp.backend.student.repository.StudentInformationRepository;
import pe.edu.utp.backend.student.repository.StudentProfileRepository;
import pe.edu.utp.backend.student.repository.StudentRepository;
import pe.edu.utp.backend.student.service.StudentProfileService;
import pe.edu.utp.backend.student.service.StudentService;
import pe.edu.utp.backend.util.cicle.model.Cicle;
import pe.edu.utp.backend.util.cicle.repository.CicleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentInformationRepository studentInformationRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CicleRepository cicleRepository;

    @Test
    public void testCompleteStudentCreationAndRetrieval() {
        // Given - Crear carrera y ciclo
        Career career = Career.builder()
                .name("Ingeniería de Sistemas")
                .code("SIS")
                .durationSemesters(10)
                .build();
        Career savedCareer = careerRepository.save(career);

        // Usar el método de fábrica para crear un ciclo Regular 1
        Cicle cicle = Cicle.createRegular1(
                "2025",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 7, 15)
        );
        Cicle savedCicle = cicleRepository.save(cicle);

        // PASO 1: Crear y guardar estudiante primero (sin información)
        Student student = Student.builder()
                .studentCode("U20210789")
                .status(Student.Status.ACTIVO)
                .career(savedCareer)
                .faculty("Ingeniería")
                .modality(Student.Modality.PRESENCIAL)
                .campus("Campus Principal")
                .enrollmentDate(LocalDate.of(2021, 3, 1))
                .lastRegistrationDate(LocalDate.of(2025, 2, 15))
                .lastEnrollmentDate(LocalDate.of(2025, 3, 1))
                .build();

        Student savedStudent = studentRepository.save(student);

        // PASO 2: Crear información del estudiante con referencia al estudiante guardado
        StudentInformation info = StudentInformation.builder()
                .student(savedStudent)  // IMPORTANTE: Asignar el estudiante guardado
                .firstName("Roberto")
                .lastName("Gómez")
                .motherLastName("Bolaños")
                .documentType(StudentInformation.DocumentType.DNI)
                .documentNumber("45678912")
                .birthDate(LocalDate.of(1990, 5, 15))
                .civilStatus(StudentInformation.CivilStatus.SOLTERO)
                .address("Av. Principal 123")
                .district("San Isidro")
                .province("Lima")
                .department("Lima")
                .emergencyContactName("Carlos Gómez")
                .emergencyContactRelationship("Padre")
                .emergencyContactPhone("987654321")
                .emergencyContactAddress("Av. Principal 123, Lima")
                .landlinePhone("01-4567890")
                .mobilePhone("987654321")
                .personalEmail("roberto@example.com")
                .build();

        // Guardar información del estudiante
        StudentInformation savedInfo = studentInformationRepository.save(info);

        // PASO 3: Actualizar relación bidireccional en el estudiante
        savedStudent.setInformation(savedInfo);
        savedStudent = studentRepository.save(savedStudent);

        // Crear perfil con todos los atributos
        StudentProfile profile = StudentProfile.builder()
                .student(savedStudent)
                .photoUrl("https://example.com/photos/roberto.jpg")
                .studentCode(savedStudent.getStudentCode())
                .fullName(savedStudent.getFullName())
                .status(savedStudent.getStatus().getDisplayName())
                .faculty(savedStudent.getFaculty())
                .modality(savedStudent.getModality().getDisplayName())
                .documentType(savedInfo.getDocumentType().getDisplayName())
                .documentNumber(savedInfo.getDocumentNumber())
                .mobilePhone(savedInfo.getMobilePhone())
                .personalEmail(savedInfo.getPersonalEmail())
                .lastUpdated(LocalDateTime.now())
                .build();

        StudentProfile savedProfile = studentProfileRepository.save(profile);

        // Then - Verificar que se guardó correctamente
        // 1. Verificar estudiante
        Optional<Student> retrievedStudent = studentRepository.findById(savedStudent.getId());
        assertTrue(retrievedStudent.isPresent());

        Student foundStudent = retrievedStudent.get();
        assertEquals("U20210789", foundStudent.getStudentCode());
        assertEquals(Student.Status.ACTIVO, foundStudent.getStatus());
        assertEquals(savedCareer.getId(), foundStudent.getCareer().getId());
        assertEquals("Ingeniería", foundStudent.getFaculty());
        assertEquals(Student.Modality.PRESENCIAL, foundStudent.getModality());
        assertEquals("Campus Principal", foundStudent.getCampus());
        assertEquals(LocalDate.of(2021, 3, 1), foundStudent.getEnrollmentDate());
        assertEquals(LocalDate.of(2025, 2, 15), foundStudent.getLastRegistrationDate());
        assertEquals(LocalDate.of(2025, 3, 1), foundStudent.getLastEnrollmentDate());

        // 2. Verificar información del estudiante
        StudentInformation foundInfo = foundStudent.getInformation();
        assertNotNull(foundInfo);
        assertEquals("Roberto", foundInfo.getFirstName());
        assertEquals("Gómez", foundInfo.getLastName());
        assertEquals("Bolaños", foundInfo.getMotherLastName());
        assertEquals(StudentInformation.DocumentType.DNI, foundInfo.getDocumentType());
        assertEquals("45678912", foundInfo.getDocumentNumber());
        assertEquals(LocalDate.of(1990, 5, 15), foundInfo.getBirthDate());
        assertEquals(StudentInformation.CivilStatus.SOLTERO, foundInfo.getCivilStatus());
        assertEquals("Av. Principal 123", foundInfo.getAddress());
        assertEquals("San Isidro", foundInfo.getDistrict());
        assertEquals("Lima", foundInfo.getProvince());
        assertEquals("Lima", foundInfo.getDepartment());
        assertEquals("Carlos Gómez", foundInfo.getEmergencyContactName());
        assertEquals("Padre", foundInfo.getEmergencyContactRelationship());
        assertEquals("987654321", foundInfo.getEmergencyContactPhone());
        assertEquals("Av. Principal 123, Lima", foundInfo.getEmergencyContactAddress());
        assertEquals("01-4567890", foundInfo.getLandlinePhone());
        assertEquals("987654321", foundInfo.getMobilePhone());
        assertEquals("roberto@example.com", foundInfo.getPersonalEmail());

        // 3. Verificar perfil del estudiante
        Optional<StudentProfile> retrievedProfile = studentProfileRepository.findById(savedProfile.getId());
        assertTrue(retrievedProfile.isPresent());

        StudentProfile foundProfile = retrievedProfile.get();
        assertEquals(savedStudent.getId(), foundProfile.getStudent().getId());
        assertEquals("https://example.com/photos/roberto.jpg", foundProfile.getPhotoUrl());
        assertEquals("U20210789", foundProfile.getStudentCode());
        assertEquals("Roberto Gómez Bolaños", foundProfile.getFullName());
        assertEquals("Activo", foundProfile.getStatus());
        assertEquals("Ingeniería", foundProfile.getFaculty());
        assertEquals("Presencial", foundProfile.getModality());
        assertEquals("DNI", foundProfile.getDocumentType());
        assertEquals("45678912", foundProfile.getDocumentNumber());
        assertEquals("987654321", foundProfile.getMobilePhone());
        assertEquals("roberto@example.com", foundProfile.getPersonalEmail());
        assertNotNull(foundProfile.getLastUpdated());
    }

    @Test
    public void testProfileSyncWithAllAttributes() {
        // Given - Crear carrera
        Career career = Career.builder()
                .name("Medicina")
                .code("MED")
                .durationSemesters(14)
                .build();
        Career savedCareer = careerRepository.save(career);

        // Crear ciclo - Usar el método de fábrica para crear un ciclo válido
        Cicle cicle = Cicle.createRegular1(
                "2025",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 7, 15)
        );
        Cicle savedCicle = cicleRepository.save(cicle);

        // PASO 1: Crear y guardar estudiante primero
        Student student = Student.builder()
                .studentCode("U20220101")
                .status(Student.Status.ACTIVO)
                .career(savedCareer)
                .faculty("Ciencias de la Salud")
                .modality(Student.Modality.PRESENCIAL)
                .campus("Campus Este")
                .enrollmentDate(LocalDate.of(2022, 3, 10))
                .lastRegistrationDate(LocalDate.of(2025, 2, 20))
                .lastEnrollmentDate(LocalDate.of(2025, 3, 5))
                .build();

        Student savedStudent = studentRepository.save(student);

        // PASO 2: Crear información del estudiante con referencia al estudiante guardado
        StudentInformation info = StudentInformation.builder()
                .student(savedStudent)  // IMPORTANTE: Asignar el estudiante guardado
                .firstName("Ana")
                .lastName("Martínez")
                .motherLastName("López")
                .documentType(StudentInformation.DocumentType.DNI)
                .documentNumber("87654321")
                .birthDate(LocalDate.of(1995, 8, 20))
                .civilStatus(StudentInformation.CivilStatus.CASADO)
                .address("Jr. Las Flores 456")
                .district("Miraflores")
                .province("Lima")
                .department("Lima")
                .emergencyContactName("Jorge Martínez")
                .emergencyContactRelationship("Hermano")
                .emergencyContactPhone("999888777")
                .emergencyContactAddress("Jr. Las Flores 456, Lima")
                .landlinePhone("01-2345678")
                .mobilePhone("999888777")
                .personalEmail("ana@example.com")
                .build();

        // Guardar información del estudiante
        StudentInformation savedInfo = studentInformationRepository.save(info);

        // PASO 3: Actualizar relación bidireccional en el estudiante
        savedStudent.setInformation(savedInfo);
        savedStudent = studentRepository.save(savedStudent);

        // When - Crear perfil usando el servicio
        StudentProfile profile = studentProfileService.createOrUpdateProfile(savedStudent);

        // Actualizar perfil con foto
        profile.setPhotoUrl("https://example.com/photos/ana.jpg");
        profile = studentProfileRepository.save(profile);

        // Then - Verificar que el perfil refleja todos los atributos del estudiante
        Optional<StudentProfile> retrievedProfile = studentProfileRepository.findByStudentId(savedStudent.getId());
        assertTrue(retrievedProfile.isPresent());

        StudentProfile foundProfile = retrievedProfile.get();
        assertEquals("U20220101", foundProfile.getStudentCode());
        assertEquals("Ana Martínez López", foundProfile.getFullName());
        assertEquals("Activo", foundProfile.getStatus());
        assertEquals("Ciencias de la Salud", foundProfile.getFaculty());
        assertEquals("Presencial", foundProfile.getModality());
        assertEquals("DNI", foundProfile.getDocumentType());
        assertEquals("87654321", foundProfile.getDocumentNumber());
        assertEquals("999888777", foundProfile.getMobilePhone());
        assertEquals("ana@example.com", foundProfile.getPersonalEmail());
        assertEquals("https://example.com/photos/ana.jpg", foundProfile.getPhotoUrl());

        // When - Actualizar información del estudiante
        savedInfo.setFirstName("Ana María");
        savedInfo.setPersonalEmail("ana.maria@example.com");
        savedInfo = studentInformationRepository.save(savedInfo);

        // Recargar el estudiante para asegurar que tenga la información actualizada
        savedStudent = studentRepository.findById(savedStudent.getId()).get();

        // Sincronizar el perfil
        StudentProfile updatedProfile = studentProfileService.createOrUpdateProfile(savedStudent);

        // Then - Verificar que el perfil se actualizó correctamente con todos los campos
        Optional<StudentProfile> retrievedUpdatedProfile = studentProfileRepository.findByStudentId(savedStudent.getId());
        assertTrue(retrievedUpdatedProfile.isPresent());

        StudentProfile foundUpdatedProfile = retrievedUpdatedProfile.get();
        assertEquals("Ana María Martínez López", foundUpdatedProfile.getFullName());
        assertEquals("ana.maria@example.com", foundUpdatedProfile.getPersonalEmail());
        // La foto debe mantenerse después de la actualización
        assertEquals("https://example.com/photos/ana.jpg", foundUpdatedProfile.getPhotoUrl());
    }

    @Test
    public void testComplexQueriesWithAllAttributes() {
        // Given - Crear datos de prueba programáticamente

        // 1. Crear carreras
        Career careerSIS = Career.builder()
                .name("Ingeniería de Sistemas")
                .code("SIS")
                .durationSemesters(10)
                .build();
        careerSIS = careerRepository.save(careerSIS);

        Career careerMED = Career.builder()
                .name("Medicina")
                .code("MED")
                .durationSemesters(14)
                .build();
        careerMED = careerRepository.save(careerMED);

        Career careerDER = Career.builder()
                .name("Derecho")
                .code("DER")
                .durationSemesters(12)
                .build();
        careerDER = careerRepository.save(careerDER);

        // 2. Crear ciclo académico
        Cicle cicle = Cicle.createRegular1(
                "2025",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 7, 15)
        );
        cicle = cicleRepository.save(cicle);

        // 3. Crear tres estudiantes completos con información

        // Estudiante 1: Juan Pérez (Ingeniería)
        Student student1 = Student.builder()
                .studentCode("U20190123")
                .status(Student.Status.ACTIVO)
                .career(careerSIS)
                .faculty("Ingeniería")
                .modality(Student.Modality.PRESENCIAL)
                .campus("Campus Principal")
                .enrollmentDate(LocalDate.of(2019, 3, 1))
                .lastRegistrationDate(LocalDate.of(2025, 2, 10))
                .lastEnrollmentDate(LocalDate.of(2025, 3, 1))
                .build();

        student1 = studentRepository.save(student1);

        StudentInformation info1 = StudentInformation.builder()
                .student(student1)
                .firstName("Juan")
                .lastName("Pérez")
                .motherLastName("García")
                .documentType(StudentInformation.DocumentType.DNI)
                .documentNumber("12345678")
                .birthDate(LocalDate.of(1995, 5, 10))
                .civilStatus(StudentInformation.CivilStatus.SOLTERO)
                .address("Av. Los Pinos 123")
                .district("San Borja")
                .province("Lima")
                .department("Lima")
                .emergencyContactName("María Pérez")
                .emergencyContactRelationship("Madre")
                .emergencyContactPhone("987654321")
                .emergencyContactAddress("Av. Los Pinos 123, Lima")
                .landlinePhone("01-3456789")
                .mobilePhone("987654321")
                .personalEmail("juan.perez@example.com")
                .build();

        info1 = studentInformationRepository.save(info1);
        student1.setInformation(info1);
        student1 = studentRepository.save(student1);

        StudentProfile profile1 = studentProfileService.createOrUpdateProfile(student1);
        profile1.setPhotoUrl("https://example.com/photos/juan.jpg");
        studentProfileRepository.save(profile1);

        // Estudiante 2: María López (Medicina)
        Student student2 = Student.builder()
                .studentCode("U20200456")
                .status(Student.Status.ACTIVO)
                .career(careerMED)
                .faculty("Medicina")
                .modality(Student.Modality.PRESENCIAL)
                .campus("Campus Este")
                .enrollmentDate(LocalDate.of(2020, 3, 1))
                .lastRegistrationDate(LocalDate.of(2025, 2, 15))
                .lastEnrollmentDate(LocalDate.of(2025, 3, 5))
                .build();

        student2 = studentRepository.save(student2);

        StudentInformation info2 = StudentInformation.builder()
                .student(student2)
                .firstName("María")
                .lastName("López")
                .motherLastName("Sánchez")
                .documentType(StudentInformation.DocumentType.DNI)
                .documentNumber("87654321")
                .birthDate(LocalDate.of(1994, 7, 15))
                .civilStatus(StudentInformation.CivilStatus.CASADO)
                .address("Jr. Las Flores 456")
                .district("Miraflores")
                .province("Lima")
                .department("Lima")
                .emergencyContactName("Carlos López")
                .emergencyContactRelationship("Hermano")
                .emergencyContactPhone("912345678")
                .emergencyContactAddress("Jr. Las Palmas 789, Lima")
                .landlinePhone("01-2345678")
                .mobilePhone("912345678")
                .personalEmail("maria.lopez@example.com")
                .build();

        info2 = studentInformationRepository.save(info2);
        student2.setInformation(info2);
        student2 = studentRepository.save(student2);

        StudentProfile profile2 = studentProfileService.createOrUpdateProfile(student2);
        profile2.setPhotoUrl("https://example.com/photos/maria.jpg");
        studentProfileRepository.save(profile2);

        // Estudiante 3: Carlos García (Derecho)
        Student student3 = Student.builder()
                .studentCode("U20180789")
                .status(Student.Status.EGRESADO)
                .career(careerDER)
                .faculty("Derecho")
                .modality(Student.Modality.SEMIPRESENCIAL)
                .campus("Campus Centro")
                .enrollmentDate(LocalDate.of(2018, 3, 1))
                .lastRegistrationDate(LocalDate.of(2023, 2, 20))
                .lastEnrollmentDate(LocalDate.of(2023, 3, 10))
                .build();

        student3 = studentRepository.save(student3);

        StudentInformation info3 = StudentInformation.builder()
                .student(student3)
                .firstName("Carlos")
                .lastName("García")
                .motherLastName("Martínez")
                .documentType(StudentInformation.DocumentType.PASSPORT)
                .documentNumber("AB123456")
                .birthDate(LocalDate.of(1992, 12, 20))
                .civilStatus(StudentInformation.CivilStatus.SOLTERO)
                .address("Calle Los Olivos 789")
                .district("San Isidro")
                .province("Lima")
                .department("Lima")
                .emergencyContactName("Pedro García")
                .emergencyContactRelationship("Padre")
                .emergencyContactPhone("956781234")
                .emergencyContactAddress("Calle Los Olivos 789, Lima")
                .landlinePhone("01-4567890")
                .mobilePhone("956781234")
                .personalEmail("carlos.garcia@example.com")
                .build();

        info3 = studentInformationRepository.save(info3);
        student3.setInformation(info3);
        student3 = studentRepository.save(student3);

        StudentProfile profile3 = studentProfileService.createOrUpdateProfile(student3);
        profile3.setPhotoUrl("https://example.com/photos/carlos.jpg");
        studentProfileRepository.save(profile3);

        // When & Then - Realizar consultas como en el test original

        // Buscar estudiantes por facultad
        List<Student> medicalStudents = studentRepository.findByFaculty("Medicina");
        assertFalse(medicalStudents.isEmpty());
        Student medStudent = medicalStudents.get(0);

        // Verificar atributos básicos
        assertNotNull(medStudent.getId());
        assertNotNull(medStudent.getStudentCode());
        assertNotNull(medStudent.getStatus());
        assertNotNull(medStudent.getFaculty());
        assertEquals("Medicina", medStudent.getFaculty());

        // Verificar información del estudiante
        assertNotNull(medStudent.getInformation());
        assertNotNull(medStudent.getInformation().getFirstName());
        assertNotNull(medStudent.getInformation().getLastName());
        assertNotNull(medStudent.getInformation().getDocumentType());
        assertNotNull(medStudent.getInformation().getDocumentNumber());

        // Buscar perfiles por nombre
        List<StudentProfile> profiles = studentProfileRepository.findByFullNameContainingIgnoreCase("García");
        assertFalse(profiles.isEmpty());
        StudentProfile profile = profiles.get(0);

        // Verificar atributos del perfil
        assertNotNull(profile.getId());
        assertNotNull(profile.getStudentCode());
        assertNotNull(profile.getFullName());
        assertTrue(profile.getFullName().contains("García"));
        assertNotNull(profile.getFaculty());
        assertNotNull(profile.getStatus());
        assertNotNull(profile.getDocumentNumber());
        assertNotNull(profile.getMobilePhone());

        // Buscar estudiantes por documento
        Optional<Student> studentByDoc = studentRepository.findByDocumentNumber("12345678");
        assertTrue(studentByDoc.isPresent());
        Student foundStudent = studentByDoc.get();
        assertNotNull(foundStudent.getInformation().getDocumentNumber());
        assertEquals("12345678", foundStudent.getInformation().getDocumentNumber());
    }
}