package cl.municipalidad.canchas.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cl.municipalidad.canchas.model.TipoCancha;

// 🏷️ Importación necesaria para documentar la clase con OpenAPI v3
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo de respuesta con la información detallada de una cancha municipal")
public class CanchaResponse {

    @Schema(description = "Identificador único autoincremental de la cancha", example = "1")
    private Integer idCancha;

    @Schema(description = "Nombre asignado al espacio deportivo", example = "Cancha Central de Pasto Sintético")
    private String nombre;

    @Schema(description = "Tipo de superficie o suelo del recinto (Valor enum)", example = "SINTETICA")
    private TipoCancha tipoDeCancha;

    @Schema(description = "Fecha en la que se dio de alta la infraestructura", example = "2026-06-25")
    private LocalDate fechaRegistro;

    @Schema(description = "Ubicación geográfica interna o dirección física del complejo", example = "Av. Olimpo #1230, Sector Las Canchas")
    private String direccion;

    @Schema(description = "El nombre del recinto deportivo en formato String directo", example = "Estadio Municipal")
    private String recinto; 

    @Schema(description = "Aforo o número máximo recomendado de jugadores concurrentes", example = "12")
    private Integer capacidad;

    @Schema(description = "Flag que define si la cancha está habilitada para agendamiento inmediato", example = "true")
    private Boolean activo;
}