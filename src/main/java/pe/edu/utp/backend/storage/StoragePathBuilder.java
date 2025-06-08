package pe.edu.utp.backend.storage;

import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.model.Section;
import pe.edu.utp.backend.content.model.Content;

import pe.edu.utp.backend.student.model.Student;

public class StoragePathBuilder {

    public static String coursePath(Course course) {
        return String.format("courses/%s_%s/", course.getCode(), sanitize(course.getName()));
    }

    public static String sectionPath(Section section) {
        return String.format("%ssections/%s_%s_%s/",
                coursePath(section.getCourse()),
                sanitize(section.getCourse().getName()),
                section.getCode(),
                section.getModality().getDisplayName()
        );
    }

    public static String weekPath(Section section, int weekNumber) {
        return String.format("%sweek_%d/", sectionPath(section), weekNumber);
    }

    public static String sessionPath(Section section, int weekNumber, int sessionNumber) {
        return String.format("%ssession_%d/", weekPath(section, weekNumber), sessionNumber);
    }

    public static String contentPath(Content content) {
        // Dependerá de cómo ligas Content a Session/Week/Section
        Section section = content.getSection();
        int weekNumber = content.getWeek().getWeekNumber();
        int sessionNumber = content.getSession().getSessionNumber();
        return sessionPath(section, weekNumber, sessionNumber);
    }

    public static String assignmentSubmissionPath(Content content, Student student, String fileExt) {
        // Usa content para navegar hacia la sesión, semana, sección, etc.
        return contentPath(content) + student.getStudentCode() + "." + fileExt;
    }

    // Agrega métodos para syllabus, materiales generales, etc.

    private static String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }
}