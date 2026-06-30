package resenas.resena.controller;




import com.proyecto.notas.dto.ApiResponse;
import com.proyecto.notas.dto.ReseñaDto;
import com.proyecto.notas.service.ResenaService;
import com.proyecto.notas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
public class ReseñasController {





    private final NotasService service;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(NotasController.class);

    @GetMapping("/list")
    @Operation(summary = "Listado de notas", description = "Permite listar todas las notas existentes (Requiere Token JWT)")
    public ResponseEntity<ApiResponse<List<NotasDto>>> listar(@RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        ApiResponse<String> validationResponse = authService.validateToken(token);

        if (validationResponse == null || validationResponse.getCode() != 200) {
            ApiResponse<List<NotasDto>> errorResponse = 
                    new ApiResponse<>(401, "Token inválido o expirado", null);
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            List<NotasDto> lista = service.listarTodas();
            ApiResponse<List<NotasDto>> response = new ApiResponse<>(200, "Listado de notas obtenido con éxito", lista);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al listar las notas: {}", e.getMessage());
            ApiResponse<List<NotasDto>> response = new ApiResponse<>(500, "Error al listar las notas: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID", description = "Permite buscar una nota específica mediante su identificador único")
    public ResponseEntity<ApiResponse<NotasDto>> obtener(@PathVariable Long id) {
        try {
            return service.buscarPorId(id)
                    .map(nota -> ResponseEntity.ok(new ApiResponse<>(200, "Nota encontrada", nota)))
                    .orElse(ResponseEntity.status(404).body(new ApiResponse<>(404, "Nota no encontrada", null)));
        } catch (Exception e) {
            ApiResponse<NotasDto> response = new ApiResponse<>(500, "Error al obtener la nota: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/crear")
    @Operation(summary = "Registrar nueva nota", description = "Permite ingresar una nueva nota al sistema")
    public ResponseEntity<ApiResponse<NotasDto>> crear(@Valid @RequestBody NotasDto notasDto) {
        try {
            NotasDto creado = service.crearNota(notasDto);
            ApiResponse<NotasDto> response = new ApiResponse<>(201, "Nota registrada exitosamente", creado);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            ApiResponse<NotasDto> response = new ApiResponse<>(400, "Error al registrar la nota: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota", description = "Permite modificar los datos de una nota existente")
    public ResponseEntity<ApiResponse<NotasDto>> actualizar(@PathVariable Long id, @Valid @RequestBody NotasDto notasDto) {
        try {
            NotasDto actualizado = service.actualizar(id, notasDto);
            return ResponseEntity.ok(new ApiResponse<>(200, "Nota actualizada correctamente", actualizado));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("No se encontró la nota")) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Nota no encontrada para actualizar", null));
            }
            ApiResponse<NotasDto> response = new ApiResponse<>(500, "Error al actualizar la nota: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota por ID", description = "Permite remover una nota del sistema usando su ID")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            if (service.buscarPorId(id).isPresent()) {
                service.eliminar(id);
                ApiResponse<Void> response = new ApiResponse<>(200, "Nota eliminada correctamente", null);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Nota no encontrada para eliminar", null));
            }
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(500, "Error al eliminar la nota: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> response = new ApiResponse<>(400, "Error de validación en los datos de la nota", errors);
        return ResponseEntity.badRequest().body(response);
    }
}