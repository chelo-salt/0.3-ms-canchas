package cl.municipalidad.canchas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cl.municipalidad.canchas.dto.request.CanchaCreateRequest;
import cl.municipalidad.canchas.dto.request.CanchaUpdateRequest;
import cl.municipalidad.canchas.dto.response.CanchaResponse;
import cl.municipalidad.canchas.service.CanchaService;
import jakarta.validation.Valid;

// 🟢 REPARADO: Importación explícita de Lombok para quitar el error del constructor
import lombok.RequiredArgsConstructor;

// 🏷️ Importaciones estándar de Swagger (Sin alias conflictivos)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/canchas")
@RequiredArgsConstructor // Ahora sí compilará perfecto
@Tag(name = "Gestión de Canchas", description = "Controlador perimetral para la administración, búsqueda y mantención de complejos deportivos municipales.")
public class CanchaController {

    private final CanchaService canchaService;

    // 1. Crear una Cancha
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva cancha", description = "Registra una nueva cancha en el sistema. Requiere rol de Administrador y validación de campos obligatorios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cancha creada exitosamente", 
            content = @Content(schema = @Schema(implementation = CanchaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Falta Token JWT o es inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Prohibido - No tienes los privilegios de ADMIN", content = @Content)
    })
    public ResponseEntity<CanchaResponse> crearCancha(
            @Valid @RequestBody CanchaCreateRequest request,
            
            // 🟢 REPARADO: Se usa la ruta completa de la anotación de Swagger para documentar el parámetro del Header
            @Parameter(description = "Email del usuario logueado inyectado por el API Gateway", required = false, example = "admin@municipalidad.cl")
            @RequestHeader(value = "X-User-Email", defaultValue = "Desconocido") String emailLogueado
    ) {
        CanchaResponse response = canchaService.guardarCancha(request, emailLogueado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    

    // 2. Obtener todas las Canchas
    @GetMapping
    @Operation(summary = "Listar todas las canchas", description = "Recupera un listado completo con todas las canchas registradas en la comuna. Endpoint de libre acceso.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", 
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CanchaResponse.class))))
    public ResponseEntity<List<CanchaResponse>> listarTodas(
            @Parameter(description = "Email opcional del usuario que consulta", example = "vecino@correo.cl")
            @RequestHeader(value = "X-User-Email", required = false) String emailLogueado) {
        System.out.println("Petición autorizada por el Gateway para el usuario: " + emailLogueado);

        List<CanchaResponse> response = canchaService.obtenerTodasCanchas(emailLogueado);
        return ResponseEntity.ok(response);
    }

    // 3. Obtener una Cancha por su ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una cancha por su ID", description = "Busca los detalles de una cancha específica mediante su identificador numérico único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancha encontrada", content = @Content(schema = @Schema(implementation = CanchaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cancha no encontrada en la base de datos municipal", content = @Content)
    })
    public ResponseEntity<CanchaResponse> obtenerPorId(
            @Parameter(description = "ID único de la cancha a consultar", required = true, example = "1")
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(canchaService.obtenerUnaCancha(id));
    }

    // 4. Actualizar una Cancha
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cancha existente", description = "Modifica los atributos físicos o de disponibilidad de una cancha mediante su ID. Operación exclusiva de ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancha modificada correctamente"),
        @ApiResponse(responseCode = "404", description = "ID de cancha no mapeado")
    })
    public ResponseEntity<CanchaResponse> actualizarCancha(
            @Parameter(description = "ID de la cancha que se desea actualizar", required = true, example = "3")
            @PathVariable("id") Integer id, 
            @Valid @RequestBody CanchaUpdateRequest request) {
        return ResponseEntity.ok(canchaService.actualizarCancha(id, request));
    }

    // 5. Eliminar una Cancha
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cancha", description = "Efectúa una baja de un recinto del inventario deportivo. Requiere privilegios elevados.")
    @ApiResponse(responseCode = "204", description = "Cancha removida de manera exitosa, sin contenido de retorno")
    public ResponseEntity<Void> eliminarCancha(
            @Parameter(description = "ID de la cancha a eliminar", required = true, example = "2")
            @PathVariable("id") Integer id) {
        canchaService.eliminarCancha(id);
        return ResponseEntity.noContent().build();
    }

    // ── ENDPOINTS DE BÚSQUEDAS PERSONALIZADAS ──

    // Buscar por nombre
    @GetMapping("/buscar")
    @Operation(summary = "Buscar canchas por texto/nombre", description = "Filtra los recintos deportivos cuyo nombre o tipo de superficie coincida parcialmente con el texto enviado.")
    @ApiResponse(responseCode = "200", description = "Resultados encontrados", 
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CanchaResponse.class))))
    public ResponseEntity<List<CanchaResponse>> buscarPorNombre(
            @Parameter(description = "Criterio de búsqueda (ej. pasto, sintética, arcilla)", required = true, example = "sintetica")
            @RequestParam("texto") String texto) {
        List<CanchaResponse> response = canchaService.buscarPorTitulo(texto);
        return ResponseEntity.ok(response);
    }

    // Buscar por ID de Recinto
    @GetMapping("/recinto/{recintoId}")
    @Operation(summary = "Listar canchas pertenecientes a un complejo o recinto", description = "Devuelve todas las canchas asociadas a una infraestructura mayor o ID de Recinto.")
    public ResponseEntity<List<CanchaResponse>> buscarPorRecinto(
            @Parameter(description = "ID del complejo deportivo global", required = true, example = "100")
            @PathVariable("recintoId") Long recintoId) {
        List<CanchaResponse> response = canchaService.buscarPorRecinto(recintoId);
        return ResponseEntity.ok(response);
    }

    // Buscar canchas de baja capacidad
    @GetMapping("/baja-capacidad")
    @Operation(summary = "Filtrar canchas por aforo máximo permitido", description = "Entrega un listado de canchas cuya capacidad de jugadores concurrentes sea menor o igual al parámetro indicado.")
    public ResponseEntity<List<CanchaResponse>> buscarBajaCapacidad(
            @Parameter(description = "Límite superior de capacidad para el filtro", required = true, 
                examples = {
                    @ExampleObject(name = "Canchas de Baby Fútbol", value = "10", description = "Filtro típico para escuadras de 5 vs 5"),
                    @ExampleObject(name = "Canchas de Tenis / Pádel", value = "4", description = "Límite para partidos de dobles")
                })
            @RequestParam("max") Integer max) {
        List<CanchaResponse> response = canchaService.buscarBajoCapacidad(max);
        return ResponseEntity.ok(response);
    }
}