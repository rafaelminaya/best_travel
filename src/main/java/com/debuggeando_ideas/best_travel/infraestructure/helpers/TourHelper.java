package com.debuggeando_ideas.best_travel.infraestructure.helpers;

import com.debuggeando_ideas.best_travel.domain.entities.*;
import com.debuggeando_ideas.best_travel.domain.repositories.ReservationRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TicketRepository;
import com.debuggeando_ideas.best_travel.infraestructure.services.ReservationService;
import com.debuggeando_ideas.best_travel.infraestructure.services.TicketService;
import com.debuggeando_ideas.best_travel.infraestructure.services.TourService;
import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
- Este helper será el encargado de crear los "tickets" y/o "reservations" para un determinado "tour".
- Esta clase helper tendrá las sgtes anotaciones:
 * @Transactional : Ya que será similar a un servicio que hará transacciones.
 * @Component : Para ser almacenado en el "contender de Spring" y usarlo mediante inyección de dependencias.
 */
@Transactional
@Component
@AllArgsConstructor
public class TourHelper {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    // Método que creará los "tickets" a partir de una colección de "vuelos" y un "cliente". Necesarios para asignarlo a un "tour.
    public Set<TicketEntity> createTickets(Set<FlyEntity> flights, CustomerEntity customerEntity) {
        // Para ser más eficiente el "HashSet", indicamos en su constructor que el tamaño incial será igual al del parámetro recbido.
        // Ya que habrá un "ticket" por cada "vuelo".
        var response = new HashSet<TicketEntity>(flights.size());
        // Recorremos los "flights"
        flights.forEach(fly -> {
            // instanciamos el ticket que contiene al "vuelo" y "cliente" obtenidos
            var ticketToPersist = TicketEntity.builder()
                    .id(UUID.randomUUID())
                    .fly(fly)
                    .customer(customerEntity)
                    .price(fly.getPrice().add(fly.getPrice().multiply(TicketService.charge_price_percentage))) // Asignamos el 25% adicional del precio del vuelo
                    .purchaseDate(LocalDate.now())
                    .departureDate(BestTravelUtil.getRandomSoon()) // Usamos la clase de "utilieria" para asignar la fecha
                    .arrivalDate(BestTravelUtil.getRandomLater()) // Usamos la clase de "utilieria" para asignar la fecha
                    .build();
            // Añadimos al "response" el "ticket" persistido en la base de datos.
            response.add(ticketRepository.save(ticketToPersist));
        });
        // Retornamos el "response" que contiene todos los tickets persistidos en la base de datos
        return response;
    }

    // Método que creará los "reservations" a partir de una colección de "hotels" y un "cliente". Necesarios para asignarlo a un "tour.
    // HashMap<HotelEntity, Integer> : Contendrá el hotel y el total de días a hospedar
    public Set<ReservationEntity> createReservations(HashMap<HotelEntity, Integer> hotels, CustomerEntity customerEntity) {
        // Para ser más eficiente el "HashSet", indicamos en su constructor que el tamaño incial será igual al del parámetro recbido.
        // Ya que habrá un "reservation" por cada "hotel".
        var response = new HashSet<ReservationEntity>(hotels.size());
        // Recorremos el hashMap "hotels", con su clave(hotel) y valor(totalDays)
        hotels.forEach((hotel, totalDays) -> {
            // instanciamos el reservation que contiene al "hotel" y "cliente" obtenidos
            var reservationToPersist = ReservationEntity.builder()
                    .id(UUID.randomUUID())
                    .hotel(hotel)
                    .customer(customerEntity)
                    .totalDays(totalDays)
                    .dateTimeReservation(LocalDateTime.now())
                    .dateStart(LocalDate.now())
                    .dateEnd(LocalDate.now().plusDays(totalDays))
                    .price(hotel.getPrice().add(hotel.getPrice().multiply(ReservationService.charge_price_percentage)))
                    .build();

            // Añadimos al "response" el "reservation" persistido en la base de datos.
            response.add(reservationRepository.save(reservationToPersist));
        });
        // Retornamos el "response" que contiene todos los reservations persistidos en la base de datos
        return response;
    }

    // Este método será usado, por medio del componente actual, para crear un "ticket".
    // Ya que se usará en el "TourService" y un servicio no puede llamar a otro servicio, pero sí a una clase helper u otro tipo de componente como este.
    public TicketEntity createTicket(FlyEntity flyEntity, CustomerEntity customerEntity){
        // instanciamos el ticket que contiene al "vuelo" y "cliente" obtenidos
        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(flyEntity)
                .customer(customerEntity)
                .price(flyEntity.getPrice().add(flyEntity.getPrice().multiply(TicketService.charge_price_percentage))) // Asignamos el 25% adicional del precio del vuelo
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon()) // Usamos la clase de "utilieria" para asignar la fecha
                .arrivalDate(BestTravelUtil.getRandomLater()) // Usamos la clase de "utilieria" para asignar la fecha
                .build();
        // retornamos el ticket persistido
        return ticketRepository.save(ticketToPersist);
    }

    public ReservationEntity createReservation(HotelEntity hotelEntity, CustomerEntity customerEntity, Integer totalDays) {

        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotelEntity)
                .customer(customerEntity)
                .totalDays(totalDays)
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(totalDays))
                .price(hotelEntity.getPrice().add(hotelEntity.getPrice().multiply(ReservationService.charge_price_percentage)))
                .build();

        return reservationRepository.save(reservationToPersist);
    }

}
