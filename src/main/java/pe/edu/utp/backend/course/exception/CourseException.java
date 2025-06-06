package pe.edu.utp.backend.course.exception;

public class CourseException extends RuntimeException {

    public CourseException(String message) {
        super(message);
    }

    public CourseException(String message, Throwable cause) {
        super(message, cause);
    }
}