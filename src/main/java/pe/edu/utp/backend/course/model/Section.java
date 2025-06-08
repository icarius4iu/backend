package pe.edu.utp.backend.course.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pe.edu.utp.backend.content.model.Content;
import pe.edu.utp.backend.student.model.Student;
import pe.edu.utp.backend.util.cicle.model.Cicle;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"course", "professors", "students", "weeks", "contents"})
@EqualsAndHashCode(of = {"id", "code"})
public class Section {


    /**
     * Enum para representar los tipos de curso
     */
    public enum Modality {
        PRESENCIAL("Presencial"),
        VIRTUAL_VIVO("Virtual en Vivo"),
        VIRTUAL_24_7("Virtual 24/7");

        private final String displayName;

        Modality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isRealTimeAttendance() {
            return this == PRESENCIAL || this == VIRTUAL_VIVO;
        }

        public boolean isAsynchronous() {
            return this == VIRTUAL_24_7;
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // Por ejemplo: "A", "B", "C"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modality modality;
    // Capacidad máxima de estudiantes
    private Integer maxStudents;

    // URL constante para sesiones virtuales si aplica
    private String virtualMeetingUrl;

    // Relación con semanas - Cambiado a Set para evitar MultipleBagFetchException
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("weekNumber ASC")
    @Builder.Default
    private Set<Week> weeks = new HashSet<>();

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
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "section_student",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "section")
    @Builder.Default
    private Set<Content> contents = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cicle_id", nullable = false)
    private Cicle cicle;

    public void addWeek(Week week) {
        if (weeks == null) {
            weeks = new HashSet<>();
        }
        weeks.add(week);
        week.setSection(this);
    }

    public void removeWeek(Week week) {
        if (weeks != null) {
            weeks.remove(week);
            week.setSection(null);
        }
    }

    public void addProfessor(Professor professor) {
        if (professors == null) {
            professors = new HashSet<>();
        }
        professors.add(professor);
        professor.getSections().add(this);
    }

    public void removeProfessor(Professor professor) {
        if (professors != null) {
            professors.remove(professor);
            professor.getSections().remove(this);
        }
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new HashSet<>();
        }
        if (maxStudents == null || students.size() < maxStudents) {
            students.add(student);
            student.getSections().add(this);
        }
    }

    public void removeStudent(Student student) {
        if (students != null) {
            students.remove(student);
            student.getSections().remove(this);
        }
    }

    public boolean isFull() {
        return maxStudents != null && students != null && students.size() >= maxStudents;
    }

    public int getAvailableSeats() {
        if (maxStudents == null) return 0;
        if (students == null) return maxStudents;
        return maxStudents - students.size();
    }

    public void generateWeeks(int weeksCount, int sessionsPerWeek) {
        if (this.weeks == null) {
            this.weeks = new HashSet<>();
        } else {
            this.weeks.clear();
        }
        LocalDate cycleStartDate = this.cicle != null ? this.cicle.getStartDate() : LocalDate.now();
        DayOfWeek startDay = DayOfWeek.MONDAY;

        for (int i = 0; i < weeksCount; i++) {
            LocalDate weekStartDate = cycleStartDate.plusWeeks(i);
            LocalDate weekEndDate = weekStartDate.plusDays(6);

            Week week = Week.builder()
                    .weekNumber(i + 1)
                    .section(this)
                    .startDate(weekStartDate)
                    .endDate(weekEndDate)
                    .build();

            for (int j = 0; j < sessionsPerWeek; j++) {
                Session.SessionType sessionType = (j % 2 == 0)
                        ? Session.SessionType.TEORIA
                        : Session.SessionType.PRACTICA;

                DayOfWeek sessionDay = startDay.plus(j % 5);

                LocalDate sessionDate = getSessionDate(weekStartDate, sessionDay, weekEndDate);
                LocalTime startTime = (j % 2 == 0)
                        ? LocalTime.of(9, 0)
                        : LocalTime.of(14, 0);
                LocalTime endTime = startTime.plusHours(2);

                Session session = Session.builder()
                        .title("Sesión " + (j + 1) + " - Semana " + (i + 1))
                        .description("Sesión de " + sessionType)
                        .type(sessionType)
                        .dayOfWeek(sessionDay)
                        .sessionDate(sessionDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .buildingName("Principal")
                        .roomNumber("A" + (j + 1))
                        .build();

                week.addSession(session);
            }
            this.addWeek(week);
        }
    }

    private LocalDate getSessionDate(LocalDate weekStart, DayOfWeek targetDay, LocalDate weekEnd) {
        LocalDate date = weekStart;
        while (date.getDayOfWeek() != targetDay) {
            date = date.plusDays(1);
            if (date.isAfter(weekEnd)) {
                return weekStart;
            }
        }
        return date;
    }

    public void generateWeeks() {
        int weeksCount = this.cicle != null ? this.cicle.getWeeksCount() : 16;
        int sessionsPerWeek = 2;
        generateWeeks(weeksCount, sessionsPerWeek);
    }

    public static SectionBuilder builder() {
        return new SectionBuilder()
                .weeks(new HashSet<>())
                .professors(new HashSet<>())
                .students(new HashSet<>())
                .contents(new HashSet<>());
    }
    /**
     * Determina si la seccion tiene asistencia presencial o en tiempo real
     */
    public boolean isRealTimeAttendance() {
        return modality != null && modality.isRealTimeAttendance();
    }

    public boolean isAsynchronous() {
        return modality != null && modality.isAsynchronous();
    }

}