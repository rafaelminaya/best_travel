package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TicketEntity implements Serializable {

    @Id
    private UUID id;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private LocalDate purchaseDate;
    private BigDecimal price; // double precision

    @ManyToOne(fetch = FetchType.LAZY) // LAZY es necesario, ya que al obtener un instancia de "TicketEntity" NO debemos obtener un "FlyEntity" automáticamente. Ya que al intentar eliminar el "TicketEntity".el objeto no será eliminado.
    @JoinColumn(name = "fly_id") // nombre para la FK en la tabla de la BD
    private FlyEntity fly;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY es necesario, ya que al obtener un instancia de "TicketEntity" NO debemos obtener un "TourEntity" automáticamente. Ya que al intentar eliminar el "TicketEntity".el objeto no será eliminado.
    @JoinColumn(name = "tour_id", nullable = true) // En algunos casos puede null, aunque esto es por defecto.
    private TourEntity tour;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY es necesario, ya que al obtener un instancia de "TicketEntity" NO debemos obtener un "CustomerEntity" automáticamente. Ya que al intentar eliminar el "TicketEntity".el objeto no será eliminado.
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
