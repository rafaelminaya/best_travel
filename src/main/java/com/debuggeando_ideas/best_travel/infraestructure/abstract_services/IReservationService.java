package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.reponses.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.ReservationRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface IReservationService extends CrudService<ReservationRequest, ReservationResponse, UUID> {
    BigDecimal findPrice(Long hotelId);
}
