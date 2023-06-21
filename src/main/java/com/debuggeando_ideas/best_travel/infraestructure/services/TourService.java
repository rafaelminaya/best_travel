package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.TourResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TourRequest;
import com.debuggeando_ideas.best_travel.domain.entities.FlyEntity;
import com.debuggeando_ideas.best_travel.domain.entities.HotelEntity;
import com.debuggeando_ideas.best_travel.domain.entities.TourEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TourRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITourService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.TourHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
// Crea un logger, con la implementación de "Simple Logging Facade for Java". Habilita la variable "log", usada en esta clase.
@Slf4j
@AllArgsConstructor
public class TourService implements ITourService {
    private final TourRepository tourRepository;
    private final FlyRepository flyRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final TourHelper tourHelper;
    private final CustomerHelper customerHelper;

    @Override
    public TourResponse create(TourRequest request) {
        // obtenemos el cliente
        var customer = customerRepository.findById(request.getCustomerId()).orElseThrow();
        // variable que contendrá los "flys" del request
        var flights = new HashSet<FlyEntity>();
        // Recorremos los "flights" del request.
        // Uno por uno los buscamos en el repositorio y agregamos al HashSet "flights".
        request.getFlights()
                .forEach(fly -> flights.add(flyRepository.findById(fly.getId()).orElseThrow()));
        //  variable HashMap que contendrá el hotel y el total de días a hospedar
        var hotels = new HashMap<HotelEntity, Integer>();
        // Recorremos los "hotels" del request.
        // Uno por uno los buscamos en el repositorio, obtenemos los días y los agregamos al HashMap "hotels".
        request.getHotels().forEach(hotel -> hotels.put(hotelRepository.findById(hotel.getId()).orElseThrow(), hotel.getTotalDays()));
        // Instanciamos el tour
        var tourToSave = TourEntity.builder()
                .tickets(this.tourHelper.createTickets(flights, customer))
                .reservations(this.tourHelper.createReservations(hotels, customer))
                .customer(customer)
                .build();
        // persistimos el objeto tour en la base de datos
        var tourSaved = tourRepository.save(tourToSave);
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.increase(customer.getDni(), TourService.class);
        // Instanciaremos y devolveremos el "TourResponse" a partir del "tour" creado.
        return TourResponse.builder()
                .id(tourSaved.getId())
                .reservationsIds(tourSaved.getReservations().stream().map(reservationEntity -> reservationEntity.getId()).collect(Collectors.toSet()))
                .ticketsIds(tourSaved.getTickets().stream().map(ticketEntity -> ticketEntity.getId()).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public TourResponse read(Long id) {
        // Obtenemos el tour
        var tourFromDB = tourRepository.findById(id).orElseThrow();
        // Retornamos el "TourEntity" al tipo "TourResponse"
        return TourResponse.builder()
                .id(tourFromDB.getId())
                .reservationsIds(tourFromDB.getReservations().stream().map(reservationEntity -> reservationEntity.getId()).collect(Collectors.toSet()))
                .ticketsIds(tourFromDB.getTickets().stream().map(ticketEntity -> ticketEntity.getId()).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void delete(Long id) {
        // Obtenemos el tour
        var tourToDelete = tourRepository.findById(id).orElseThrow();
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.decrease(tourToDelete.getCustomer().getDni(), TourService.class);
        // Eliminamos el tour obtenido del repositorio
        tourRepository.delete(tourToDelete);
    }

    @Override
    public UUID addTicket(Long tourId, Long flyId) {
        // Obtenemos el "tour" a actualizar
        var tourUpdate = tourRepository.findById(tourId).orElseThrow();
        // Obtenemos el "fly" que será agregado al "ticket"
        var fly = flyRepository.findById(flyId).orElseThrow();
        // creamos un nuevo "ticket" con el "fly" obtenido y lo persistimos.
        // usaremos una clase "helper" ya que un método "service" no debe invocar a otro método "service".
        var ticket = this.tourHelper.createTicket(fly, tourUpdate.getCustomer());
        // agregamos el nuevo ticket al tour
        tourUpdate.addTicket(ticket);
        // persistimos los cambios
        tourRepository.save(tourUpdate);
        // retornamos el id del ticket que se persistió
        return ticket.getId();
    }

    @Override
    public void removeTicket(Long tourId, UUID ticketId) {
        // Obtenemos el "tour" a actualizar
        var tourUpdate = tourRepository.findById(tourId).orElseThrow();
        // eliminamos el objeto "ticket" del objeto "tour"
        tourUpdate.removeTicket(ticketId);
        // persistimos los cambios del "tour" en la base de datos
        tourRepository.save(tourUpdate);
    }

    @Override
    public UUID addReservation(Long tourId, Long hotelId, Integer totalDays) {
        // Obtenemos el "tour" a actualizar
        var tourUpdate = tourRepository.findById(tourId).orElseThrow();
        // Obtenemos el "hotel" que será agregado al "ticket"
        var hotel = hotelRepository.findById(hotelId).orElseThrow();
        // creamos un nuevo "reservation" con el "hotel" obtenido y lo persistimos.
        // usaremos una clase "helper" ya que un método "service" no debe invocar a otro método "service".
        var reservation = this.tourHelper.createReservation(hotel, tourUpdate.getCustomer(), totalDays);
        // agregamos el nuevo ticket al tour
        tourUpdate.addReservation(reservation);
        // persistimos los cambios
        tourRepository.save(tourUpdate);
        // retornamos el id del ticket que se persistió
        return reservation.getId();
    }

    @Override
    public void removeReservation(Long tourId, UUID reservationId) {
        // Obtenemos el "tour" a actualizar
        var tourUpdate = tourRepository.findById(tourId).orElseThrow();
        // eliminamos el objeto "reservation" del objeto "tour"
        tourUpdate.removeReservation(reservationId);
        // persistimos los cambios del "tour" en la base de datos
        tourRepository.save(tourUpdate);
    }
}