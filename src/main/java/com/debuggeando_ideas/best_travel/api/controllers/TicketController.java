package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.TicketResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TicketRequest;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "ticket")
@Tag(name = "Ticket")
// Anotación de OpenApi-Swagger para modificar el subtítulo de este controlador en la interfaz gráfica.
public class TicketController {
    private final ITicketService ticketService;

    /*
    - ResponseEntity :
    * Es el objeto spring que se encarga de responder el status http, headers, etc.
    * Recomendado usarlo al exponer un servicio en Spring.
    - @ApiResponse : Anotación de OpenApi-Swagger para indicar qué devolver al obtener un error.
    - content :
    * Permite añadir más  "Schema" en la interfaz gráfica de OperApi-Swagger
    * En este caso añdiremos los DTOs faltantes del response de error. (ErrorResponse, ErrorsResponse)
    - @Operation : Anotación propia de OpenApi-Swagger para añadir descripción de este controlador en la interfaz gráfica.
     */
    @ApiResponse(
            responseCode = "400",
            description = "When the request have a field invalid we response this",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @Operation(summary = "Save in system un ticket with the fly passed in parameter")
    @PostMapping
    public ResponseEntity<TicketResponse> post(@Valid @RequestBody TicketRequest ticketRequest) {
//        return ResponseEntity.ok(ticketService.create(ticketRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(ticketRequest));
    }

    @Operation(summary = "Return a ticket with id passed")
    @GetMapping(path = "{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.read(id));
    }

    @Operation(summary = "Update a ticket")
    @PutMapping(path = "{id}")
    public ResponseEntity<TicketResponse> put(@PathVariable UUID id, @Valid @RequestBody TicketRequest ticketRequest) {
//        return ResponseEntity.ok(ticketService.update(ticketRequest, id));
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.update(ticketRequest, id));
    }

    @Operation(summary = "Delete a ticket with id passed")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Return a ticket price given a fly id")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getTicketPrice(
            @RequestParam Long flyId,
            @RequestHeader(required = false) Currency currency) {
        if (Objects.isNull(currency)) {
            currency = Currency.getInstance("USD");
        }
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        return ResponseEntity.ok(Collections.singletonMap("TicketPrice", ticketService.findPrice(flyId, currency)));
    }
}
