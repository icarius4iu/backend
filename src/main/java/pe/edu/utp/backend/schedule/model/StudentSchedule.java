package pe.edu.utp.backend.schedule.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.student.model.Student;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_schedules")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"entries", "student"}) // Excluir referencias bidireccionales
@EqualsAndHashCode(of = {"id"}) // Usar SOLO el ID para hashCode y equals
public class    StudentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"information", "schedule"})
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonIgnoreProperties("studentSchedule")
    @OneToMany(mappedBy = "studentSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ScheduleEntry> entries = new HashSet<>();



    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled;

    /**
     * Añade una entrada al horario
     */
    public void addEntry(ScheduleEntry entry) {
        entries.add(entry);
        entry.setStudentSchedule(this);
    }

    /**
     * Elimina una entrada del horario
     */
    public void removeEntry(ScheduleEntry entry) {
        entries.remove(entry);
        entry.setStudentSchedule(null);
    }

    /**
     * Actualiza la marca de tiempo de última actualización
     */
    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }
}