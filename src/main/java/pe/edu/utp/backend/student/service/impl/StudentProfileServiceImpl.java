package pe.edu.utp.backend.student.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.StudentProfile;
import pe.edu.utp.backend.student.repository.StudentProfileRepository;
import pe.edu.utp.backend.student.repository.StudentRepository;
import pe.edu.utp.backend.student.service.StudentProfileService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public StudentProfileServiceImpl(
            StudentProfileRepository studentProfileRepository,
            StudentRepository studentRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentProfile save(StudentProfile studentProfile) {
        return studentProfileRepository.save(studentProfile);
    }

    @Override
    public StudentProfile createOrUpdateProfile(Student student) {
        // Buscar si ya existe un perfil para este estudiante
        Optional<StudentProfile> existingProfile =
                studentProfileRepository.findByStudentId(student.getId());

        StudentProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new StudentProfile();
            profile.setStudent(student);
        }

        // Sincronizar datos desde el estudiante
        profile.syncFromStudent();

        return studentProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfile> findById(Long id) {
        return studentProfileRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfile> findByStudentId(Long studentId) {
        return studentProfileRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfile> findByStudentCode(String studentCode) {
        return studentProfileRepository.findByStudentCode(studentCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfile> findByDocumentNumber(String documentNumber) {
        return studentProfileRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> searchByName(String nameQuery) {
        return studentProfileRepository.findByFullNameContainingIgnoreCase(nameQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> findByFaculty(String faculty) {
        return studentProfileRepository.findByFaculty(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> findAll() {
        return studentProfileRepository.findAll();
    }

    @Override
    public StudentProfile updateProfilePhoto(Long id, String photoUrl) {
        Optional<StudentProfile> profileOpt = studentProfileRepository.findById(id);
        if (profileOpt.isPresent()) {
            StudentProfile profile = profileOpt.get();
            profile.setPhotoUrl(photoUrl);
            return studentProfileRepository.save(profile);
        }
        // En un caso real, aquí lanzaríamos una excepción
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> findUpdatedAfter(LocalDateTime date) {
        return studentProfileRepository.findByLastUpdatedAfter(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> findAllWithPhotos() {
        return studentProfileRepository.findAllWithPhotos();
    }

    @Override
    public void delete(Long id) {
        studentProfileRepository.deleteById(id);
    }

    @Override
    public void syncAllProfiles() {
        List<Student> allStudents = studentRepository.findAll();
        for (Student student : allStudents) {
            createOrUpdateProfile(student);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getFacultyStatistics() {
        List<Object[]> results = studentProfileRepository.countByFaculty();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            String faculty = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(faculty, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentId(Long studentId) {
        return studentProfileRepository.findByStudentId(studentId).isPresent();
    }
}