package pe.edu.utp.backend.storage.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.backend.course.model.Course;
import pe.edu.utp.backend.course.repository.CourseRepository;
import pe.edu.utp.backend.storage.StoragePathBuilder;
import pe.edu.utp.backend.storage.dto.CourseDTO;
import pe.edu.utp.backend.storage.dto.CreateCourseRequest;


@Service
@RequiredArgsConstructor
public class CourseService2 {

    private final CourseRepository courseRepository;
    private final FirebaseStorageService firebaseStorageService;


    public CourseDTO createCourse(CreateCourseRequest request) {
        // 1. Crear entidad y guardar en BD
        Course course = Course.builder()
                .code(request.getCode())
                .name(request.getName())
                .credits(request.getCredits())
                .description(request.getDescription())
                .weeklyHours(request.getWeeklyHours())
                // ...asigna ciclo, carreras, etc.
                .build();
        courseRepository.save(course);

        // 2. Crear carpeta lógica en Storage
        String path = StoragePathBuilder.coursePath(course); // "courses/INF1022232_InformáticaI/"
        firebaseStorageService.createFolderIfNotExists(path);

        // 3. Devuelve DTO de respuesta
        return new CourseDTO(course.getId(), course.getCode(), course.getName());
    }
}