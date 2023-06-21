package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.reponses.TourResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TourRequest;

import java.util.UUID;

public interface ITourService extends SimpleCrudService<TourRequest, TourResponse, Long>{
    UUID addTicket(Long tourId, Long flyId);
    void removeTicket(Long tourId, UUID ticketId);
    UUID addReservation(Long tourId, Long hotelId, Integer totalDays);
    void removeReservation(Long tourId, UUID reservationId);
}
