package pe.edu.utp.backend.util.cicle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cicle {
    /**
     * Enum para representar los tipos de ciclo: REGULAR o VERANO
     */
    public enum CicleType {
        REGULAR, VERANO
    }

    /**
     * Enum para representar el periodo dentro del ciclo regular
     */
    public enum RegularPeriod {
        REGULAR_1, REGULAR_2
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String year;            // Año académico (ejemplo: "2025")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CicleType type;         // Tipo de ciclo (REGULAR o VERANO)

    @Enumerated(EnumType.STRING)
    private RegularPeriod period;   // Solo aplica si el tipo es REGULAR

    @Column(nullable = false)
    private LocalDate startDate;    // Fecha de inicio del ciclo

    @Column(nullable = false)
    private LocalDate endDate;      // Fecha de fin del ciclo

    @Column(nullable = false, unique = true)
    private String name;            // Nombre del ciclo (formato: "VERANO_2025" o "REGULAR_2025-I" o "REGULAR_2025-II")

    /**
     * Obtiene el número de semanas del ciclo según su tipo
     * @return 18 para ciclo REGULAR, 10 para ciclo VERANO
     */
    public int getWeeksCount() {
        return type == CicleType.REGULAR ? 18 : 10;
    }

    /**
     * Verifica si el ciclo es de tipo Regular
     */
    public boolean isRegular() {
        return type == CicleType.REGULAR;
    }

    /**
     * Verifica si el ciclo es de tipo Verano
     */
    public boolean isVerano() {
        return type == CicleType.VERANO;
    }

    /**
     * Genera el nombre del ciclo según el formato establecido
     */
    public String generateCicleName() {
        if (type == CicleType.REGULAR) {
            // Para ciclos regulares: REGULAR_2025-I o REGULAR_2025-II
            String romanNumber = (period == RegularPeriod.REGULAR_1) ? "I" : "II";
            return "REGULAR_" + year + "-" + romanNumber;
        } else {
            // Para ciclos de verano: VERANO_2025
            return "VERANO_" + year;
        }
    }

    /**
     * Inicializa el nombre del ciclo si aún no se ha establecido
     */
    public void initializeName() {
        if (name == null || name.isEmpty()) {
            this.name = generateCicleName();
        }
    }

    /**
     * Método de fábrica para crear un ciclo Regular 1
     */
    public static Cicle createRegular1(String year, LocalDate startDate, LocalDate endDate) {
        Cicle cicle = Cicle.builder()
                .year(year)
                .type(CicleType.REGULAR)
                .period(RegularPeriod.REGULAR_1)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        cicle.setName("REGULAR_" + year + "-I");
        return cicle;
    }

    /**
     * Método de fábrica para crear un ciclo Regular 2
     */
    public static Cicle createRegular2(String year, LocalDate startDate, LocalDate endDate) {
        Cicle cicle = Cicle.builder()
                .year(year)
                .type(CicleType.REGULAR)
                .period(RegularPeriod.REGULAR_2)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        cicle.setName("REGULAR_" + year + "-II");
        return cicle;
    }

    /**
     * Método de fábrica para crear un ciclo de Verano
     */
    public static Cicle createVerano(String year, LocalDate startDate, LocalDate endDate) {
        Cicle cicle = Cicle.builder()
                .year(year)
                .type(CicleType.VERANO)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        cicle.setName("VERANO_" + year);
        return cicle;
    }

    /**
     * Genera una representación de cadena para el ciclo actual
     */
    @Override
    public String toString() {
        // Usar el nombre si está disponible
        if (name != null && !name.isEmpty()) {
            return name;
        }

        return generateCicleName();
    }
}