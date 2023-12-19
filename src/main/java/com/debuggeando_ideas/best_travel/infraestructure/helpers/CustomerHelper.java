package com.debuggeando_ideas.best_travel.infraestructure.helpers;

import com.debuggeando_ideas.best_travel.domain.repositories.jpa.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@AllArgsConstructor
public class CustomerHelper {
    private final CustomerRepository customerRepository;

    // Método que actuaizará los totales de "flys", "reservations" y "tours" del "customer"
    // Los argumentos serán el id del customer y el nombre de la clase "service" utilizada previamente.
    public void increase(String customerId, Class<?> type) {
        var customerToUpdate = customerRepository.findById(customerId).orElseThrow();

        switch (type.getSimpleName()) {
            case "TourService" -> customerToUpdate.setTotalTours(customerToUpdate.getTotalTours() + 1);
            case "TicketService" -> customerToUpdate.setTotalFlights(customerToUpdate.getTotalFlights() + 1);
            case "ReservationService" -> customerToUpdate.setTotalLodgings(customerToUpdate.getTotalLodgings() + 1);
        }
        customerRepository.save(customerToUpdate);
    }

    public void decrease(String customerId, Class<?> type) {
        var customerToUpdate = customerRepository.findById(customerId).orElseThrow();

        switch (type.getSimpleName()) {
            case "TourService" -> customerToUpdate.setTotalTours(customerToUpdate.getTotalTours() - 1);
            case "TicketService" -> customerToUpdate.setTotalFlights(customerToUpdate.getTotalFlights() - 1);
            case "ReservationService" -> customerToUpdate.setTotalLodgings(customerToUpdate.getTotalLodgings() - 1);
        }
        customerRepository.save(customerToUpdate);
    }
}
