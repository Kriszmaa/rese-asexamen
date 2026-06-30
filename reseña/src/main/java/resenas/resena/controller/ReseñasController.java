package resenas.resena.controller;




import resenas.resena.*;
import resenas.resena.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/api/reseñas")
@RequiredArgsConstructor
public class ReseñasController<ReseñaDto> {





    private final ReseñasService service;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(ReseñasController.class);

    @GetMapping("/list")
    @Operation(summary = "Listado de reseñas", description = "Permite listar todas las reseñas existentes (Requiere Token JWT)")
    public ResponseEntity<ApiResponse<List<ReseñaDto>>> listar(@RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        ApiResponse<String> validationResponse = authService.validateToken(token);

        if (validationResponse == null || validationResponse.getCode() != 200) {
            ApiResponse<List<ReseñaDto>> errorResponse = 
                    new ApiResponse<>(401, "Token inválido o expirado", null);
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            List<ReseñaDto> lista = service.listarTodas();
            ApiResponse<List<ReseñaDto>> response = new ApiResponse<>(200, "Listado de reseñas obtenido con éxito", lista);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al listar las reseñas: {}", e.getMessage());
            ApiResponse response = new ApiResponse<>(500, "Error al listar las reseñas: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reseña por ID", description = "Permite buscar una reseña específica mediante su identificador único")
    public ResponseEntity<ApiResponse<ReseñaDto>> obtener(@PathVariable Long id) {
        try {
            return service.buscarPorId(id)
                    .map(nota -> ResponseEntity.ok(new ApiResponse<>(200, "Reseña encontrada", nota)))
                    .orElse(ResponseEntity.status(404).body(new ApiResponse<>(404, "Reseña no encontrada", null)));
        } catch (Exception e) {
            ApiResponse<ReseñaDto> response = new ApiResponse<>(500, "Error al obtener la reseña: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/crear")
    @Operation(summary = "Registrar nueva reseña", description = "Permite ingresar una nueva reseña al sistema")
    public ResponseEntity<ApiResponse<ReseñaDto>> crear(@Valid @RequestBody ReseñaDto reseñaDto) {
        try {
            ReseñaDto creado = service.crearReseña(reseñaDto);
            ApiResponse<ReseñaDto> response = new ApiResponse<>(201, "Reseña registrada exitosamente", creado);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            ApiResponse<ReseñaDto> response = new ApiResponse<>(400, "Error al registrar la reseña: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reseña", description = "Permite modificar los datos de una reseña existente")
    public ResponseEntity<ApiResponse<ReseñaDto>> actualizar(@PathVariable Long id, @Valid @RequestBody ReseñaDto reseñaDto) {
        try {
            ReseñaDto actualizado = service.actualizar(id, reseñaDto);
            return ResponseEntity.ok(new ApiResponse<>(200, "Reseña actualizada correctamente", actualizado));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("No se encontró la reseña")) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Reseña no encontrada para actualizar", null));
            }
            ApiResponse<ReseñaDto> response = new ApiResponse<>(500, "Error al actualizar la reseña: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña por ID", description = "Permite remover una reseña del sistema usando su ID")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            if (service.buscarPorId(id).isPresent()) {
                service.eliminar(id);
                ApiResponse<Void> response = new ApiResponse<>(200, "Reseña eliminada correctamente", null);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Reseña no encontrada para eliminar", null));
            }
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(500, "Error al eliminar la reseña: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> response = new ApiResponse<>(400, "Error de validación en los datos de la reseña", errors);
        return ResponseEntity.badRequest().body(response);
    }
}