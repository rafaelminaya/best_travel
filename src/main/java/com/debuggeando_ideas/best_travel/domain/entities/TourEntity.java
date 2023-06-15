package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name = "tour")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            mappedBy = "tour" // relación bidireccional con el atributo "fly" de la clase "TicketEntity"
    )
    private Set<ReservationEntity> reservations;

    // Exclusión, en el método ".toString()" de los atributos anotados con "@OneToMany" para evitar la recursividad infinita
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            mappedBy = "tour" // relación bidireccional con el atributo "fly" de la clase "TicketEntity"
    )
    private Set<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "id_customer") // nombre para la FK en la tabla de la BD
    private CustomerEntity customer;

    // MÉTODOS

    public void addTicket(TicketEntity ticket) {
        // Validación de que el listado de "tickets" no sea "null"
        if (Objects.isNull(this.tickets)) {
            this.tickets = new HashSet<>();
        }
        this.tickets.add(ticket);
    }

    public void removeTicket(UUID ticketId) {
        // Validación de que el listado de "tickets" no sea "null"
        if (Objects.isNull(this.tickets)) {
            this.tickets = new HashSet<>();
        }
        this.tickets.removeIf(ticket -> ticket.getId().equals(ticketId));
    }


    public void updateTickets() {
        this.tickets.forEach(ticket -> ticket.setTour(this));
    }

    public void addReservation(ReservationEntity reservation) {
        // Validación de que el listado de "reservations" no sea "null"
        if (Objects.isNull(this.reservations)) {
            this.reservations = new HashSet<>();
        }
        this.reservations.add(reservation);
    }

    public void removeReservation(UUID reservationId) {
        // Validación de que el listado de "reservations" no sea "null"
        if (Objects.isNull(this.reservations)) {
            this.reservations = new HashSet<>();
        }
        this.reservations.removeIf(reservation -> reservation.getId().equals(reservationId));
    }

    public void updateReservations() {
        this.reservations.forEach(reservation -> reservation.setTour(this));
    }
}
