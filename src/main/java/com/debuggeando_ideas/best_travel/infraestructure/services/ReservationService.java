package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.ReservationRequest;
import com.debuggeando_ideas.best_travel.domain.entities.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.ReservationRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.ApiCurrencyConnectorHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.EmailHelper;
import com.debuggeando_ideas.best_travel.util.enums.Tables;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
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
    private final BlackListHelper blackListHelper;
    private final ApiCurrencyConnectorHelper currencyConnectorHelper;
    private final EmailHelper emailHelper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        // Método validador que se encarga de verificar si el cliente está en la "lista negra" de no poder hacer transacciones
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

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
        // validación en caso el email del parámetro obtenido no sea null.
        if (Objects.nonNull(request.getEmail())) {
            // Invocamos a la función que envía el email
            this.emailHelper.sendMail(request.getEmail(), customer.getFullName(), Tables.reservation.name());
        }

        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        var reservatioFromDB = reservationRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));

        return this.entityToResponse(reservatioFromDB);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID uuid) {
        // obtenemos el vuelo
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        // obtenemos la reservation a actualizar
        var reservationToUpdate = reservationRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
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
        var reservationToDelete = reservationRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.decrease(reservationToDelete.getCustomer().getDni(), ReservationService.class);
        // persistimos el objeto en la base de datos
        reservationRepository.delete(reservationToDelete);
    }

    @Override
    public BigDecimal findPrice(Long hotelId, Currency currency) {
        var hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        var priceInDollars = hotel.getPrice().add(hotel.getPrice().multiply(charge_price_percentage));

        // validación del parámetro "currency" ya que es opcional. Lo devolveremos en dólares en caso no llegue este parámetro.
        if (currency.equals(Currency.getInstance("USD"))) {
            return priceInDollars;
        }
        // Obtenemos la respuesta de nuestro "web client" enviando el parámetro obtenido
        var currencyDTO = this.currencyConnectorHelper.getCurrency(currency);
        log.info("API currency in {}, response: {}", currencyDTO.getExchangeDate().toString(), currencyDTO.getRates());
        // Retornamos el precio en dólares multiplicado por el valor obtenido del mapa, cuyo "key" será el parámetro "currency"
        return priceInDollars.multiply(currencyDTO.getRates().get(currency));
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

    public static final BigDecimal charge_price_percentage = BigDecimal.valueOf(0.20);
}
