package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerEntity implements Serializable {

    @Id
    private String dni;
    @Column(length = 50)
    private String fullName;
    @Column(length = 20)
    private String creditCard;
    @Column(length = 12)
    private String phoneNumber;
    private Integer totalFlights;
    private Integer totalLodgings;
    private Integer totalTours;

    // Exclusión, en el método ".toString()" de los atributos anotados con "@OneToMany" para evitar la recursividad infinita
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, // LAZY para permitir el "delete" luego del "decrease" en el "TourService", "ReservationService" y "TicketService"
            orphanRemoval = true,
            mappedBy = "customer" // relación bidireccional con el atributo "customer" de la clase "TicketEntity"
    )
    private Set<TicketEntity> tickets;

    // Exclusión, en el método ".toString()" de los atributos anotados con "@OneToMany" para evitar la recursividad infinita
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, // LAZY para permitir el "delete" luego del "decrease" en el "TourService", "ReservationService" y "TicketService"
            orphanRemoval = true,
            mappedBy = "customer" // relación bidireccional con el atributo "customer" de la clase "ReservationEntity"
    )
    private Set<ReservationEntity> reservations;

    // Exclusión, en el método ".toString()" de los atributos anotados con "@OneToMany" para evitar la recursividad infinita
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, // Usamos LAZY, funciona sin problemas el método "remove()" de JPA que con EAGER
            orphanRemoval = true,
            mappedBy = "customer" // relación bidireccional con el atributo "customer" de la clase "TourEntity"
    )
    private Set<TourEntity> tours;
}
