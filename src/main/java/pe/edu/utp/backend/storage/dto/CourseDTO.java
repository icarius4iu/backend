    package pe.edu.utp.backend.storage.dto;


    import lombok.AllArgsConstructor;
    import lombok.Data;

    @Data
    @AllArgsConstructor
    public class CourseDTO {
        private Long id;
        private String code;
        private String name;
    }