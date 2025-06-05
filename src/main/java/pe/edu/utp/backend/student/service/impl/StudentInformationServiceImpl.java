package pe.edu.utp.backend.student.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.backend.student.model.StudentInformation;
import pe.edu.utp.backend.student.model.StudentInformation.DocumentType;
import pe.edu.utp.backend.student.repository.StudentInformationRepository;
import pe.edu.utp.backend.student.service.StudentInformationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class StudentInformationServiceImpl implements StudentInformationService {

    private final StudentInformationRepository studentInformationRepository;

    @Autowired
    public StudentInformationServiceImpl(StudentInformationRepository studentInformationRepository) {
        this.studentInformationRepository = studentInformationRepository;
    }

    @Override
    public StudentInformation save(StudentInformation studentInformation) {
        return studentInformationRepository.save(studentInformation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentInformation> findById(Long id) {
        return studentInformationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentInformation> findByDocumentNumber(String documentNumber) {
        return studentInformationRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentInformation> findByDocumentTypeAndNumber(DocumentType type, String number) {
        return studentInformationRepository.findByDocumentTypeAndDocumentNumber(type, number);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentInformation> findByPersonalEmail(String email) {
        return studentInformationRepository.findByPersonalEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentInformation> searchByFullName(String fullNameQuery) {
        return studentInformationRepository.findByFullNameContaining(fullNameQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentInformation> findByLastName(String lastName) {
        return studentInformationRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentInformation> findByDepartment(String department) {
        return studentInformationRepository.findByDepartment(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentInformation> findAll() {
        return studentInformationRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        studentInformationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<DocumentType, Long> getDocumentTypeStatistics() {
        List<Object[]> results = studentInformationRepository.countByDocumentType();
        Map<DocumentType, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            DocumentType type = (DocumentType) result[0];
            Long count = (Long) result[1];
            statistics.put(type, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocument(String documentNumber) {
        return studentInformationRepository.findByDocumentNumber(documentNumber).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return studentInformationRepository.findByPersonalEmail(email).isPresent();
    }
}