package pe.edu.utp.backend.course.service.base;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public abstract class BaseAcademicService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    @Autowired
    protected EntityManager entityManager; // Si ya existe este campo, no necesitas inyectarlo nuevamente en WeekService


    protected BaseAcademicService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad con ID " + id + " no encontrada"));
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Transactional
    public void deleteById(ID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Entidad con ID " + id + " no encontrada para eliminar");
        }
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    public long count() {
        return repository.count();
    }
}