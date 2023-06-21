package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.TicketResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TicketRequest;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(path = "ticket")
public class TicketController {
    private final ITicketService ticketService;

    // ResponseEntity : Es el objeto spring que se encarga de responder el status http, headers, etc.
    // Recomendado usarlo al exponer un servicio en Spring.
    @PostMapping
    public ResponseEntity<TicketResponse> post(@RequestBody TicketRequest ticketRequest) {
        return ResponseEntity.ok(ticketService.create(ticketRequest));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.read(id));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<TicketResponse> put(@PathVariable UUID id, @RequestBody TicketRequest ticketRequest) {
        return ResponseEntity.ok(ticketService.update(ticketRequest, id));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getTicketPrice(@RequestParam Long flyId) {
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        return ResponseEntity.ok(Collections.singletonMap("TicketPrice", ticketService.findPrice(flyId)));
    }
}
