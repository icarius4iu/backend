package pe.edu.utp.backend.auth.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.utp.backend.student.model.Student.Modality;
import pe.edu.utp.backend.student.model.StudentInformation.DocumentType;
import pe.edu.utp.backend.student.model.StudentInformation.CivilStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    // Campos de StudentInformation
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String lastName;

    private String motherLastName;

    @NotNull(message = "El tipo de documento es obligatorio")
    private DocumentType documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    private String documentNumber;

    private LocalDate birthDate;

    private CivilStatus civilStatus;

    private String address;

    private String district;

    private String province;

    private String department;

    private String emergencyContactName;

    private String emergencyContactRelationship;

    private String emergencyContactPhone;

    private String emergencyContactAddress;

    private String landlinePhone;

    @NotBlank(message = "El teléfono celular es obligatorio")
    private String mobilePhone;

    @NotBlank(message = "El correo electrónico personal es obligatorio")
    @Email(message = "El formato de correo electrónico no es válido")
    private String personalEmail;

    // Campos de Student
    @NotNull(message = "La carrera es obligatoria")
    private Long careerId;

    @NotNull(message = "La modalidad es obligatoria")
    private Modality modality;

    @NotBlank(message = "El campus es obligatorio")
    private String campus;

    // Campos adicionales para autenticación
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}