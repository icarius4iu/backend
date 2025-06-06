package pe.edu.utp.backend.course.exception;

public class EnrollmentException extends CourseException {

    public EnrollmentException(String message) {
        super(message);
    }

    public EnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}