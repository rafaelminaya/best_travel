package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.ReservationRequest;
import com.debuggeando_ideas.best_travel.domain.entities.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.ReservationRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
// Crea un logger, con la implementación de "Simple Logging Facade for Java". Habilita la variable "log", usada en esta clase.
@Slf4j
@AllArgsConstructor
public class ReservationService implements IReservationService {
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerHelper customerHelper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(request.getTotalDays())
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(request.getTotalDays()))
                .price(hotel.getPrice().add(hotel.getPrice().multiply(charge_price_percentage)))
                .build();

        var reservationPersisted = reservationRepository.save(reservationToPersist);
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.increase(customer.getDni(), ReservationService.class);

        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        var reservatioFromDB = reservationRepository.findById(uuid).orElseThrow();

        return this.entityToResponse(reservatioFromDB);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID uuid) {
        // obtenemos el vuelo
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow();
        // obtenemos la reservation a actualizar
        var reservationToUpdate = reservationRepository.findById(uuid).orElseThrow();
        // actualizamos la información del reservation
        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setTotalDays(request.getTotalDays());
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(request.getTotalDays()));
        reservationToUpdate.setPrice(hotel.getPrice().add(hotel.getPrice().multiply(charge_price_percentage)));
        // persistimos el objeto en la base de datos.
        var reservationUpdated = reservationRepository.save(reservationToUpdate);
        log.info("Reservation updated with id {}", reservationUpdated.getId());
        // retornamos el reservation persistido mapeado en un DTO "ReservationRequest"
        return this.entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        // Obtenemos el ticket el reservation
        var reservationToDelete = reservationRepository.findById(uuid).orElseThrow();
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.decrease(reservationToDelete.getCustomer().getDni(), ReservationService.class);
        // persistimos el objeto en la base de datos
        reservationRepository.delete(reservationToDelete);
    }

    // Método encargado de mapear las entidades a un DTO response("ReservationEntity" a "ReservationResponse")
    private ReservationResponse entityToResponse(ReservationEntity reservationEntity) {
        var reservationResponse = new ReservationResponse();
        /*
        BeanUtils
        - Librería de Spring, encagada de comparar los atirbutos en común y asignar el valor de esas variables.
        - La otra alternativa sería asignar cada atributo con los métodos setters.
        copyProperties(reservationEntity, reservationResponse)
        - El primer argumento es el objeto de la fuente de datos.
        - El segundo argumento es el objeto al cual se le asignarán los valores en común
         */
        BeanUtils.copyProperties(reservationEntity, reservationResponse);

        var hotelResponse = new HotelResponse();
        // Asignando los valores del "HotelEntity" al "HotelResponse"
        BeanUtils.copyProperties(reservationEntity.getHotel(), hotelResponse);
        reservationResponse.setHotel(hotelResponse);

        return reservationResponse;
    }

    @Override
    public BigDecimal findPrice(Long hotelId) {
        var hotel = hotelRepository.findById(hotelId).orElseThrow();

        return hotel.getPrice().add(hotel.getPrice().multiply(charge_price_percentage));
    }

    public static final BigDecimal charge_price_percentage = BigDecimal.valueOf(0.20);
}
