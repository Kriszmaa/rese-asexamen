package resenas.resena.dto;

import jakarta.validation.constraints.*;
import lombok.*;


@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReseñasDto {

    private Long id;

    @NotBlank(message = "El nombre de usuario  es obligatorio")
    private String nombreUser;

    @NotBlank(message = "La area de servicio es obligatoria")
    private String areaServicio;

    @NotNull(message = "La valoracion es obligatoria")
    @DecimalMin(value = "1.0", message = "La valoracion mínima es 1.0")
    @DecimalMax(value = "5.0", message = "La valoracion máxima es 5.0")
    private Double valor;

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;
}
