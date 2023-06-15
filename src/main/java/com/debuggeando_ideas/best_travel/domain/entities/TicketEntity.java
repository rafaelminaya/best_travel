package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private LocalDateTime purchaseDate;
    private BigDecimal price; // double precision

    @ManyToOne
    @JoinColumn(name = "fly_id") // nombre para la FK en la tabla de la BD
    private FlyEntity fly;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = true) // En algunos casos puede null, aunque esto es por defecto.
    private TourEntity tour;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
