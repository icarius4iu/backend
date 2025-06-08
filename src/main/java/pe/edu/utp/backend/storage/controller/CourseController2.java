package pe.edu.utp.backend.storage.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.backend.storage.dto.CourseDTO;
import pe.edu.utp.backend.storage.service.CourseService2;
import pe.edu.utp.backend.storage.dto.CreateCourseRequest;
import pe.edu.utp.backend.storage.service.FirebaseStorageService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController2{

    private final CourseService2 courseService2;
    private final FirebaseStorageService firebaseStorageService;


    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CreateCourseRequest request) {
        CourseDTO created = courseService2.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}