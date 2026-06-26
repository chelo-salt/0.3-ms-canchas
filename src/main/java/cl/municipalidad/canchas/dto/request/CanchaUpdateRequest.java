package cl.municipalidad.canchas.dto.request;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cl.municipalidad.canchas.model.TipoCancha;

// 🏷️ Importación necesaria para OpenAPI v3
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload o estructura con los campos editables para modificar las propiedades de una cancha existente")
public class CanchaUpdateRequest {

    @NotBlank(message = "El nombre de la cancha es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nuevo nombre modificado para la cancha (Min: 3, Max: 100 caracteres)", 
            example = "Estadio Municipal - Cancha Central Renombrada")
    private String nombre;

    @NotNull(message = "El tipo de cancha es obligatorio")
    @Schema(description = "Actualización del tipo de superficie o suelo (Valor Enum)", example = "PASTO_NATURAL")
    private TipoCancha tipoDeCancha;

    @NotNull(message = "La fecha de registro es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o una fecha futura")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Fecha de reprogramación o actualización. Formato estricto: dd-MM-yyyy", example = "30-06-2026")
    private LocalDate fechaRegistro;

    @NotBlank(message = "La dirección de la ubicación es obligatoria")
    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Schema(description = "Modificación de la dirección física si corresponde", example = "Av. Las Torres #452, Sector Oriente")
    private String direccion;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    @Max(value = 100000, message = "Capacidad demasiado alta para un recinto municipal")
    @Schema(description = "Modificación del aforo máximo permitido (Rango: 1 - 100,000)", example = "22")
    private Integer capacidad;
}