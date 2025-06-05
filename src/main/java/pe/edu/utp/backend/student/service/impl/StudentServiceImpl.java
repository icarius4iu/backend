package pe.edu.utp.backend.student.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.career.model.Career;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.student.model.Student.Status;
import pe.edu.utp.backend.student.model.Student.Modality;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.repository.StudentRepository;
import pe.edu.utp.backend.student.service.StudentInformationService;
import pe.edu.utp.backend.student.service.StudentService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentInformationService studentInformationService;

    @Autowired
    public StudentServiceImpl(
            StudentRepository studentRepository,
            StudentInformationService studentInformationService) {
        this.studentRepository = studentRepository;
        this.studentInformationService = studentInformationService;
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student createStudent(Student student, StudentInformation information) {
        // Guardar primero la información del estudiante
        StudentInformation savedInfo = studentInformationService.save(information);

        // Establecer la relación bidireccional
        student.setInformation(savedInfo);

        // Guardar el estudiante
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByDocumentNumber(String documentNumber) {
        return studentRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByStatus(Status status) {
        return studentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByCareer(Career career) {
        return studentRepository.findByCareer(career);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByCareerAndStatus(Career career, Status status) {
        return studentRepository.findByCareerAndStatus(career, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByFaculty(String faculty) {
        return studentRepository.findByFaculty(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByModality(Modality modality) {
        return studentRepository.findByModality(modality);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByCampus(String campus) {
        return studentRepository.findByCampus(campus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchByName(String nameQuery) {
        return studentRepository.findByFullNameContaining(nameQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student updateStatus(Long id, Status newStatus) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setStatus(newStatus);
            return studentRepository.save(student);
        }
        // En un caso real, aquí lanzaríamos una excepción
        return null;
    }

    @Override
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCareerStatistics() {
        List<Object[]> results = studentRepository.countByCareerName();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            String careerName = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(careerName, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Status, Long> getStatusStatistics() {
        List<Object[]> results = studentRepository.countByStatus();
        Map<Status, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            Status status = (Status) result[0];
            Long count = (Long) result[1];
            statistics.put(status, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Modality, Long> getModalityStatistics() {
        List<Object[]> results = studentRepository.countByModality();
        Map<Modality, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            Modality modality = (Modality) result[0];
            Long count = (Long) result[1];
            statistics.put(modality, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode).isPresent();
    }
}