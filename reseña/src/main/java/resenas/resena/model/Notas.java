package main.java.resenas.resena.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "reseñas")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @NotBlank(message = "el nombre de usuario es obligatorio")
    @Column(nullable = false)
    private String nombreUser;

    
    @NotBlank(message = "el area de servicio es obligatorio")
    @Column(nullable = false)
    private String areaServicio;
    
    
    @NotNull(message = "la valoracion es  obligatoria")
    @DecimalMin(value = "1", message = "la valoracion minima es 1 estrella")
    @DecimalMax(value = "5", message = "5 estrellas es la valoracion maxima")
    @Column(nullable = false)
    private Double valoracion;
}
