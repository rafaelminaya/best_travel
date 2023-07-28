package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.TicketResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TicketRequest;
import com.debuggeando_ideas.best_travel.domain.entities.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TicketRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.ApiCurrencyConnectorHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.EmailHelper;
import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import com.debuggeando_ideas.best_travel.util.enums.Tables;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
// Crea un logger, con la implementación de "Simple Logging Facade for Java". Habilita la variable "log", usada en esta clase
@Slf4j
@AllArgsConstructor
public class TicketService implements ITicketService {
    private final FlyRepository flyRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final CustomerHelper customerHelper;
    private final BlackListHelper blackListHelper;
    private final ApiCurrencyConnectorHelper currencyConnectorHelper;
    private final EmailHelper emailHelper;

    @Override
    public TicketResponse create(TicketRequest request) {
        // Método validador que se encarga de verificar si el cliente está en la "lista negra" de no poder hacer transacciones
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        // Obtenemos el vuelo
        var fly = flyRepository.findById(request.getIdFly()).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        // Obtenemos el cliente
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));
        // instanciamos el ticket que contiene al "vuelo" y "cliente" obtenidos
        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(charge_price_percentage))) // Asignamos el 25% adicional del precio del vuelo
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon()) // Usamos la clase de "utilieria" para asignar la fecha
                .arrivalDate(BestTravelUtil.getRandomLater()) // Usamos la clase de "utilieria" para asignar la fecha
                .build();

        // persistimos el ticket en la base de datos
        var ticketPersisted = ticketRepository.save(ticketToPersist);
        // {} : Símbolo que se reemplazará por el segundo parámetro que es "ticketPersisted.getId()"
        log.info("Ticket saved with id: {}", ticketPersisted.getId());
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.increase(customer.getDni(), TicketService.class);
        // validación en caso el email del parámetro obtenido no sea null.
        if (Objects.nonNull(request.getEmail())) {
            // Invocamos a la función que envía el email
            this.emailHelper.sendMail(request.getEmail(), customer.getFullName(), Tables.ticket.name());
        }
        // retornamos el ticket persistido mapeado en un DTO "TicketResponse"
        return this.entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID uuid) {
        var ticketFromDB = ticketRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));

        return this.entityToResponse(ticketFromDB);
    }

    @Override
    public TicketResponse update(TicketRequest request, UUID uuid) {
        // obtenemos el ticket a actualizar
        var ticketToUpdate = ticketRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        // obtenemos el vuelo
        var fly = flyRepository.findById(request.getIdFly()).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        // actualizamos la información del ticket
        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().add(fly.getPrice().multiply(charge_price_percentage)));// Asignamos el 25% adicional del precio del vuelo
        ticketToUpdate.setDepartureDate(BestTravelUtil.getRandomSoon()); // Usamos la clase de "utilieria" para asignar la fecha
        ticketToUpdate.setArrivalDate(BestTravelUtil.getRandomLater()); // Usamos la clase de "utilieria" para asignar la fecha
        // persistimos el objeto en la base de datos.
        var ticketUpdated = ticketRepository.save(ticketToUpdate);
        log.info("Ticket updated with id {}", ticketUpdated.getId());
        // retornamos el ticket persistido mapeado en un DTO "TicketResponse"
        return this.entityToResponse(ticketUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        // Obtenemos el ticket a eliminar
        var ticketToDelete = ticketRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        // invocamos al método que actualiza los totales de "fly", "reservations" y "tours" del customer
        customerHelper.decrease(ticketToDelete.getCustomer().getDni(), TicketService.class);
        // persistimos el objeto en la base de datos
        ticketRepository.delete(ticketToDelete);
    }

    @Override
    public BigDecimal findPrice(Long flyId, Currency currency) {
        var fly = flyRepository.findById(flyId).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));

        var priceInDollars = fly.getPrice().add(fly.getPrice().multiply(charge_price_percentage)); // Asignamos el 25% adicional del precio del vuelo
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

    // Método encargado de mapear las entidades a un DTO response("TicketEntity" a "TicketResponse")
    private TicketResponse entityToResponse(TicketEntity ticketEntity) {
        var ticketResponse = new TicketResponse();
        /*
        BeanUtils
        - Librería de Spring, encagada de comparar los atirbutos en común y asignar el valor de esas variables.
        - La otra alternativa sería asignar cada atributo con los métodos setters.
        copyProperties(ticketEntity, ticketResponse)
        - El primer argumento es el objeto de la fuente de datos.
        - El segundo argumento es el objeto al cual se le asignarán los valores en común
         */
        BeanUtils.copyProperties(ticketEntity, ticketResponse);

        var flyReponse = new FlyResponse();
        // Asignando los valores del "FlyEntity" al "FlyResponse"
        BeanUtils.copyProperties(ticketEntity.getFly(), flyReponse);
        ticketResponse.setFly(flyReponse);

        return ticketResponse;
    }

    // Constante que representa el porcentaje para a añadir en el precio del ticket
    public static final BigDecimal charge_price_percentage = BigDecimal.valueOf(0.25);
}
