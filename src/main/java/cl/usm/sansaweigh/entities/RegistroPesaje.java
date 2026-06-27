package cl.usm.sansaweigh.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "registros_pesaje")
public class RegistroPesaje implements Serializable {

    @Id
    private String id;

    private Integer balanzaId;
    private String paqueteId;

    @Transient
    private Double pesoKg; // peso de entrada en kg, no se guarda

    private Double pesoSansas;

    private CategoriaPeso categoria;
    private EstadoPesaje estado;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
