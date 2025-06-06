package pe.edu.utp.backend.course.exception;

public class InvalidAcademicEntityException extends CourseException {

    public InvalidAcademicEntityException(String message) {
        super(message);
    }

    public InvalidAcademicEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}