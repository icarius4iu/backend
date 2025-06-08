package pe.edu.utp.backend.student.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.util.career.model.Career;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.schedule.model.StudentSchedule;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"information", "sections", "schedule"})
@EqualsAndHashCode(of = {"id", "studentCode"}) // Solo usar campos identificadores
@JsonIgnoreProperties({"sections"})
public class Student {



    /**
     * Estado del estudiante
     */
    public enum Status {
        ACTIVO("Activo"),
        INACTIVO("Inactivo"),
        EGRESADO("Egresado");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Modalidad de estudio
     */
    public enum Modality {
        PRESENCIAL("Presencial"),
        SEMIPRESENCIAL("Semipresencial"),
        VIRTUAL("Virtual");

        private final String displayName;

        Modality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id")
    private Career career;

    private String faculty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modality modality;

    private String campus;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "last_registration_date")
    private LocalDate lastRegistrationDate;

    @Column(name = "last_enrollment_date")
    private LocalDate lastEnrollmentDate;

    @JsonIgnoreProperties("student")
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentInformation information;

    // Relación con secciones (desde la clase Section)
    @ManyToMany(mappedBy = "students")
    @Builder.Default
    private Set<Section> sections = new HashSet<>();

    // Relación con el horario
    @JsonIgnoreProperties("student")
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentSchedule schedule;

    /**
     * Obtiene el nombre completo del estudiante
     */
    public String getFullName() {
        if (information != null) {
            return information.getFirstName() + " " +
                    information.getLastName() + " " +
                    information.getMotherLastName();
        }
        return "Estudiante " + studentCode;
    }

    /**
     * Verifica si el estudiante está activo
     */
    public boolean isActive() {
        return status == Status.ACTIVO;
    }

    /**
     * Verifica si el estudiante ha egresado
     */
    public boolean hasGraduated() {
        return status == Status.EGRESADO;
    }

    /**
     * Establece la información del estudiante y mantiene la relación bidireccional
     */
    public void setInformation(StudentInformation information) {
        this.information = information;
        if (information != null) {
            information.setStudent(this);
        }
    }
}