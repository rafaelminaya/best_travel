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

@Entity(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReservationEntity implements Serializable {
    // Generación de UUID desde el código de Java, los IDs de 'Hotel' y 'Fly', sí serán generados desde la BD
    @Id
    private UUID id;
    @Column(name = "date_reservation")
    private LocalDateTime dateTimeReservation;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Integer totalDays;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "hotel_id") // nombre para la FK en la tabla de la BD
    private HotelEntity hotel;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = true) // En algunos casos puede null, aunque esto es por defecto.
    private TourEntity tour;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
