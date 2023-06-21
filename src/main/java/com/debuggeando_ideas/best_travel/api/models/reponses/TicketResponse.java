package com.debuggeando_ideas.best_travel.api.models.reponses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TicketResponse implements Serializable {
    private UUID id;
    /*
    Librería jackson
    - Spring, para serializar en JSON, utiliza a la librería jackson.
    - Esta librería permite utilizar diferentes anotaciones para dar formatos.
    - shape = JsonFormat.Shape.STRING : Indica que será de tipo "String".
    - pattern = "yyyy-MM-dd HH:mm" : Formato indicado para el atributo.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime departureDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime arrivalDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    private BigDecimal price;
    private FlyResponse fly;
}
