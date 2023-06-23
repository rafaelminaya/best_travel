package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name = "tour")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourEntity implements Serializable {

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
    // Método para crear la relación inversa, asignando el tour actual en cada "reservation" y "ticket".
    // @PrePersist : Automáticamente se ejecutará este método cada vez que se haga una "persistencia" a la base de datos.
    // @PreRemove : Automáticamente se ejecutará este método cada vez que se haga una "remove" a la base de datos.
    // Estos métodos tienen que ser "void" y sin parámetros.
    @PrePersist
    @PreRemove
    public void updateFk() {
        this.tickets.forEach(ticket -> ticket.setTour(this));
        this.reservations.forEach(reservation -> reservation.setTour(this));
    }

    // Método para agregar un "ticket" del "tour"
    public void addTicket(TicketEntity ticketEntity) {
        // Validación de que el listado de "tickets" no sea "null"
        if (Objects.isNull(this.tickets)) {
            this.tickets = new HashSet<>();
        }
        // Añadimos el nuevo ticket a la instancia
        this.tickets.add(ticketEntity);
        // Recorremos los tickets asignándole la instancia del "tour" en cada ticket
        // Esto es necesario para hacer la relación inversa.
        this.tickets.forEach(ticket -> ticket.setTour(this));
    }

    // Método para eliminar un "ticket" del "tour"
    public void removeTicket(UUID uuid) {
        // Validación de que el listado de "tickets" no sea "null"
        if (Objects.isNull(this.tickets)) {
            this.tickets = new HashSet<>();
        }
        // Iteramos los tickets buscando el del parámetro
        this.tickets.forEach(ticket -> {
            // En caso encontrarlo, le asignamos "null"
            if (ticket.getId().equals(uuid)) {
                ticket.setTour(null);
            }
        });
    }

    public void addReservation(ReservationEntity reservationEntity) {
        // Validación de que el listado de "reservations" no sea "null"
        if (Objects.isNull(this.reservations)) {
            this.reservations = new HashSet<>();
        }
        // Añadimos el nuevo reservations a la instancia
        this.reservations.add(reservationEntity);
        // Recorremos los reservationss asignándole la instancia del "tour" en cada reservation.
        // Esto es necesario para hacer la relación inversa.
        this.reservations.forEach(reservation -> reservation.setTour(this));
    }

    // Método para eliminar un "ticket" del "tour"
    public void removeReservation(UUID uuid) {
        // Validación de que el listado de "tickets" no sea "null"
        if (Objects.isNull(this.reservations)) {
            this.reservations = new HashSet<>();
        }
        // Iteramos los tickets buscando el del parámetro
        this.reservations.forEach(reservation -> {
            // En caso encontrarlo, le asignamos "null"
            if (reservation.getId().equals(uuid)) {
                reservation.setTour(null);
            }
        });
    }
 /*
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
    */

    public void updateTickets() {
        this.tickets.forEach(ticket -> ticket.setTour(this));
    }

    public void updateReservations() {
        this.reservations.forEach(reservation -> reservation.setTour(this));
    }
}
