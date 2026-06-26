package cl.municipalidad.canchas.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cl.municipalidad.canchas.model.TipoCancha;

// 🏷️ Importación necesaria para OpenAPI v3
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload o estructura requerida para registrar y dar de alta una nueva cancha en el sistema municipal")
public class CanchaCreateRequest {

    @NotBlank(message = "El nombre de la cancha es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre distintivo del espacio deportivo (Min: 3, Max: 100 caracteres)", 
            example = "Cancha de Baby Fútbol N° 1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotNull(message = "El tipo de cancha es obligatorio")
    @Schema(description = "Tipo de superficie o suelo del complejo (Valor Enum)", 
            example = "SINTETICA", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoCancha tipoDeCancha;

    @NotNull(message = "La fecha de registro es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o una fecha futura")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Fecha de alta del recinto. Formato estricto: dd-MM-yyyy (Debe ser hoy o una fecha futura)", 
            example = "25-06-2026", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaRegistro;

    @NotBlank(message = "La dirección de la ubicación es obligatoria")
    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Schema(description = "Ubicación geográfica o dirección física del complejo (Max: 150 caracteres)", 
            example = "Av. Las Torres #450, Sector Oriente", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;

    @NotNull(message = "El ID del recinto es obligatorio")
    @Positive(message = "El ID del recinto debe ser mayor a 0")
    @Schema(description = "ID único identificador del complejo municipal mayor al que pertenece (Debe ser mayor a 0)", 
            example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long recintoId;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad de usuarios o espectadores debe ser mayor a 0")
    @Max(value = 100000, message = "Capacidad demasiado alta para un recinto municipal")
    @Schema(description = "Aforo máximo permitido de deportistas concurrentes (Rango: 1 - 100,000)", 
            example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer capacidad;
}