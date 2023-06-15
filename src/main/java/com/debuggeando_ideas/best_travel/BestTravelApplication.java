package com.debuggeando_ideas.best_travel;

import com.debuggeando_ideas.best_travel.domain.entities.FlyEntity;
import com.debuggeando_ideas.best_travel.domain.entities.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.entities.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.entities.TourEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@Slf4j
// Crea un logger, con la implementación de "Simple Logging Facade for Java". Habilita la variable "log", usada en la línea 28.
public class BestTravelApplication implements CommandLineRunner {

    private final HotelRepository hotelRepository;

    private final FlyRepository flyRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    public BestTravelApplication(
            HotelRepository hotelRepository,
            FlyRepository flyRepository,
            TicketRepository ticketRepository,
            ReservationRepository reservationRepository,
            TourRepository tourRepository,
            CustomerRepository customerRepository) {
        this.hotelRepository = hotelRepository;
        this.flyRepository = flyRepository;
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(BestTravelApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        var fly = flyRepository.findById(15L).get();
        var hotel = hotelRepository.findById(7L).get();
        var ticket = ticketRepository.findById(UUID.fromString("32345678-1234-5678-4234-567812345678")).get();
        var reservation = reservationRepository.findById(UUID.fromString("52345678-1234-5678-1234-567812345678")).get();
        var customer = customerRepository.findById("BBMB771012HMCRR022").get();

        // Parsemos al tipo "String", ya que es el tipo de dato que usa el argumento de "info()"
        log.info(String.valueOf(fly));
        log.info(String.valueOf(hotel));
        log.info(String.valueOf(ticket));
        log.info(String.valueOf(reservation));
        log.info(String.valueOf(customer));
        */
//        this.flyRepository.selectLessPrice(BigDecimal.valueOf(20)).forEach(System.out::println);
//        this.flyRepository.selectBetweenPrice(BigDecimal.valueOf(10), BigDecimal.valueOf(15)).forEach(System.out::println);
//        this.flyRepository.selectOriginDestiny("Grecia", "Mexico").forEach(System.out::println);
//        this.flyRepository.selectOriginDestiny("Grecia", "Mexico").forEach(fly -> log.info(String.valueOf(fly)));


        /*
        var fly = flyRepository.findById(1L).get();
        System.out.println(fly);
        fly.getTickets().forEach(ticket-> System.out.println(ticket));
        */

        /*
        var fly = flyRepository.findByTicketId(UUID.fromString("12345678-1234-5678-2236-567812345678")).get();
        System.out.println(fly);
        */

//        this.hotelRepository.findByPriceLessThan(BigDecimal.valueOf(100)).forEach(System.out::println);
//        this.hotelRepository.findByPriceBetween(BigDecimal.valueOf(100), BigDecimal.valueOf(150)).forEach(System.out::println);
//        this.hotelRepository.findByRatingGreaterThan(3).forEach(System.out::println);

//        var hotel = this.hotelRepository.findByReservationId(UUID.fromString("12345678-1234-5678-1234-567812345678")).get();
//        System.out.println(hotel);

        // Creando un nuevo Tour
        var customer = customerRepository.findById("GOTW771012HMRGR087").orElseThrow();
        log.info("Client name: " + customer.getFullName());

        var fly = flyRepository.findById(11L).orElseThrow();
        log.info("Hotel: " + fly.getOriginName() + " - " + fly.getDestinyName());

        var hotel = hotelRepository.findById(3L).orElseThrow();
        log.info("Fly: " + hotel.getName());


        var tour = TourEntity.builder()
                .customer(customer)
                .build();

        var ticket = TicketEntity.builder().id(UUID.randomUUID())
                .price(fly.getPrice().multiply(BigDecimal.TEN))
                .arrivalDate(LocalDateTime.now())
                .departureDate(LocalDateTime.now())
                .purchaseDate(LocalDateTime.now())
                .customer(customer)
                .tour(tour)
                .fly(fly)
                .build();

        var reservation = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now().plusDays(1))
                .dateEnd(LocalDate.now().plusDays(2))
                .hotel(hotel)
                .customer(customer)
                .tour(tour)
                .totalDays(1)
                .price(hotel.getPrice().multiply(BigDecimal.TEN))
                .build();

        System.out.println("--- SAVING ---");
        // 1) Agregamos la "reservation" al "tour"
        tour.addReservation(reservation);
        // 2) relación inversa del "tour" con las "reservations" para crear la relación
        tour.updateReservations();
        //3) Agregamos el "ticket" al "tour"
        tour.addTicket(ticket);
        // 2) relación inversa del "tour" con los "tickets" para crear la relación
        tour.updateTickets();

        var tourSaved = this.tourRepository.save(tour);
        Thread.sleep(8000);
        this.tourRepository.deleteById(tourSaved.getId());

    }
}

