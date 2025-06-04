package pe.edu.utp.backend.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_information")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInformation {

    /**
     * Tipos de documento de identidad
     */
    public enum DocumentType {
        DNI("DNI"),
        CE("Carné de Extranjería"),
        PASSPORT("Pasaporte"),
        OTHER("Otro");

        private final String displayName;

        DocumentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Estados civiles
     */
    public enum CivilStatus {
        SOLTERO("Soltero/a"),
        CASADO("Casado/a"),
        DIVORCIADO("Divorciado/a"),
        VIUDO("Viudo/a"),
        CONVIVIENTE("Conviviente");

        private final String displayName;

        CivilStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String motherLastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "civil_status")
    private CivilStatus civilStatus;

    private String address;

    private String district;

    private String province;

    private String department;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_relationship")
    private String emergencyContactRelationship;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_address")
    private String emergencyContactAddress;

    @Column(name = "landline_phone")
    private String landlinePhone;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "personal_email")
    private String personalEmail;

    /**
     * Obtiene la dirección completa
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        if (address != null && !address.isEmpty()) {
            sb.append(address);
        }

        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }

        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province);
        }

        if (department != null && !department.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(department);
        }

        return sb.toString();
    }

    /**
     * Obtiene la información completa de contacto de emergencia
     */
    public String getEmergencyContactInfo() {
        StringBuilder sb = new StringBuilder();

        if (emergencyContactName != null && !emergencyContactName.isEmpty()) {
            sb.append(emergencyContactName);

            if (emergencyContactRelationship != null && !emergencyContactRelationship.isEmpty()) {
                sb.append(" (").append(emergencyContactRelationship).append(")");
            }

            if (emergencyContactPhone != null && !emergencyContactPhone.isEmpty()) {
                sb.append(" - Tel: ").append(emergencyContactPhone);
            }

            if (emergencyContactAddress != null && !emergencyContactAddress.isEmpty()) {
                sb.append(" - Dir: ").append(emergencyContactAddress);
            }
        }

        return sb.toString();
    }
}