package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.ReservationRequest;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(path = "reservation")
public class ReservationController {
    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> post(@Valid @RequestBody ReservationRequest reservationRequest){
        return ResponseEntity.ok(reservationService.create(reservationRequest));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.read(id));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> put(@PathVariable UUID id, @Valid @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(reservationService.update(reservationRequest, id));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getReservationPrice(@RequestParam Long hotelId) {
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        return ResponseEntity.ok(Collections.singletonMap("ReservationPrice", reservationService.findPrice(hotelId)));
    }
}
