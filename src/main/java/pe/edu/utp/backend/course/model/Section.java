package pe.edu.utp.backend.course.model;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.student.model.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"course", "professors", "students", "weeks", "contents"})
@EqualsAndHashCode(exclude = {"course", "professors", "students", "weeks", "contents"})
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // Por ejemplo: "A", "B", "C"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Capacidad máxima de estudiantes
    private Integer maxStudents;

    // URL constante para sesiones virtuales si aplica
    private String virtualMeetingUrl;

    // Relación con semanas
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("weekNumber ASC")
    @Builder.Default
    private List<Week> weeks = new ArrayList<>();

    // Relación muchos a muchos con Profesor
    @ManyToMany
    @JoinTable(
            name = "section_professor",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    @Builder.Default
    private Set<Professor> professors = new HashSet<>();

    // Relación muchos a muchos con Estudiante
    @ManyToMany
    @JoinTable(
            name = "section_student",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "section")
    private List<Content> contents = new ArrayList<>();

    /**
     * Añade una semana a la sección
     */
    public void addWeek(Week week) {
        weeks.add(week);
        week.setSection(this);
    }

    /**
     * Elimina una semana de la sección
     */
    public void removeWeek(Week week) {
        weeks.remove(week);
        week.setSection(null);
    }

    /**
     * Añade un profesor a la sección
     */
    public void addProfessor(Professor professor) {
        professors.add(professor);
        professor.getSections().add(this);
    }

    /**
     * Elimina un profesor de la sección
     */
    public void removeProfessor(Professor professor) {
        professors.remove(professor);
        professor.getSections().remove(this);
    }

    /**
     * Añade un estudiante a la sección
     */
    public void addStudent(Student student) {
        if (students.size() < maxStudents) {
            students.add(student);
            student.getSections().add(this);
        }
    }

    /**
     * Elimina un estudiante de la sección
     */
    public void removeStudent(Student student) {
        students.remove(student);
        student.getSections().remove(this);
    }

    /**
     * Verifica si la sección ha alcanzado su capacidad máxima
     */
    public boolean isFull() {
        return maxStudents != null && students.size() >= maxStudents;
    }

    /**
     * Obtiene el número de vacantes disponibles
     */
    public int getAvailableSeats() {
        return maxStudents != null ? maxStudents - students.size() : 0;
    }

    /**
     * Genera todas las semanas para la sección basado en el ciclo del curso
     */
    public void generateWeeks() {
        if (course != null && course.getCicle() != null) {
            int totalWeeks = course.getCicle().getWeeksCount();
            for (int i = 1; i <= totalWeeks; i++) {
                Week week = new Week();
                week.setWeekNumber(i);
                week.setTitle("Semana " + i);
                this.addWeek(week);
            }
        }
    }
}