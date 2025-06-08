package pe.edu.utp.backend.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.backend.schedule.model.ScheduleAlert;

public interface ScheduleAlertRepository extends JpaRepository<ScheduleAlert, Long> {
    // Puedes agregar métodos custom si los necesitas
}