    package pe.edu.utp.backend.schedule.controller;

    import lombok.RequiredArgsConstructor;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import pe.edu.utp.backend.course.model.Section;
    import pe.edu.utp.backend.schedule.dtos.ReprogramRequestDTO;
    import pe.edu.utp.backend.schedule.dtos.ScheduleConflictDTO;
    import pe.edu.utp.backend.schedule.dtos.ScheduleDTO;
    import pe.edu.utp.backend.schedule.dtos.StudentScheduleDTO;
    import pe.edu.utp.backend.schedule.mapper.ScheduleDTOMapper;
    import pe.edu.utp.backend.schedule.model.ScheduleEntry;
    import pe.edu.utp.backend.schedule.model.StudentSchedule;
    import pe.edu.utp.backend.schedule.service.ScheduleService;
    import pe.edu.utp.backend.student.model.Student;
    import pe.edu.utp.backend.student.service.StudentService;
    import pe.edu.utp.backend.util.cicle.model.Cicle;

    import java.time.LocalDate;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/schedules")
    @RequiredArgsConstructor
    public class ScheduleController {

        private final ScheduleService scheduleService;
        private final ScheduleDTOMapper dtoMapper;
        private final StudentService studentService; // Asegúrate de inyectar este servicio

        @GetMapping("/students/{studentId}/cicle")
        public ResponseEntity<?> getCicleSchedule(@PathVariable Long studentId) {
            try {
                Student student = studentService.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

                if (student.getSections().isEmpty()) {
                    return ResponseEntity.ok(Map.of(
                            "message", "El estudiante no está matriculado en ninguna sección",
                            "studentId", studentId,
                            "studentName", student.getFullName(),
                            "timestamp", "2025-06-07 14:26:35"
                    ));
                }

                // Obtener el ciclo de la primera sección que tenga uno definido
                Cicle cicle = null;
                for (Section section : student.getSections()) {
                    if (section.getCicle() != null) {
                        cicle = section.getCicle();
                        break;
                    }
                }

                if (cicle == null) {
                    return ResponseEntity.ok(Map.of(
                            "message", "No se pudo determinar el ciclo académico para el estudiante",
                            "studentId", studentId,
                            "studentName", student.getFullName(),
                            "timestamp", "2025-06-07 14:26:35"
                    ));
                }

                System.out.println("🔍 Obteniendo horario para ciclo: " + cicle.getName() +
                        " (" + cicle.getStartDate() + " a " + cicle.getEndDate() + ")");

                // Buscar entradas en el rango de fechas del ciclo
                StudentScheduleDTO dto = scheduleService.getScheduleForDateRange(
                        studentId,
                        cicle.getStartDate(),
                        cicle.getEndDate()
                );

                System.out.println("📊 Entradas encontradas: " +
                        (dto.getEntries() != null ? dto.getEntries().size() : 0));

                return ResponseEntity.ok(dto);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "error", e.getMessage(),
                                "timestamp", "2025-06-07 14:26:35",
                                "user", "icarius4iu"
                        ));
            }
        }

        @PostMapping("/students/{studentId}/update")
        public ResponseEntity<StudentSchedule> updateScheduleFromSections(@PathVariable Long studentId) {
            StudentSchedule updated = scheduleService.updateScheduleFromSections(studentId);
            return ResponseEntity.ok(updated);
        }

        @GetMapping("/students/{studentId}/week")
        public ResponseEntity<ScheduleDTO> getWeeklySchedule(
                @PathVariable Long studentId,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {

            // Si no se especifica fecha, usar la fecha actual
            if (weekStart == null) {
                weekStart = LocalDate.now();
            }

            ScheduleDTO schedule = scheduleService.getWeeklySchedule(studentId, weekStart);
            return ResponseEntity.ok(schedule);
        }

        @GetMapping("/conflicts")
        public ResponseEntity<List<ScheduleConflictDTO>> checkEnrollmentConflicts(
                @RequestParam Long studentId,
                @RequestParam Long sectionId) {

            List<ScheduleConflictDTO> conflicts = scheduleService.checkScheduleConflicts(studentId, sectionId);
            return ResponseEntity.ok(conflicts);
        }

        @PostMapping("/entries")
        public ResponseEntity<ScheduleEntry> addScheduleEntry(
                @RequestBody ScheduleEntry entry) {
            // Implementar lógica para agregar entrada personalizada
            return ResponseEntity.ok(entry);
        }

        @DeleteMapping("/entries/{entryId}")
        public ResponseEntity<Map<String, Object>> removeScheduleEntry(@PathVariable Long entryId) {
            // Implementar lógica para eliminar entrada
            Map<String, Object> response = new HashMap<>();
            response.put("deleted", true);
            return ResponseEntity.ok(response);
        }
        @PostMapping("/reprogram")
        public ResponseEntity<Void> reprogramScheduleEntry(@RequestBody ReprogramRequestDTO dto) {
            scheduleService.reprogramScheduleEntry(
                    dto.getScheduleEntryId(),
                    dto.getNewDate(),
                    dto.getNewStartTime(),
                    dto.getNewEndTime(),
                    dto.getNewRoomNumber(),
                    dto.getReason()
            );
            return ResponseEntity.ok().build();
        }
    }