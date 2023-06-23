package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.ReservationRequest;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservation") // Anotación de OpenApi-Swagger para modificar el subtítulo de este controlador en la interfaz gráfica.
public class ReservationController {
    private final IReservationService reservationService;

    /*
    - @ApiResponse :
    * Anotación de OpenApi-Swagger para indicar qué devolver al obtener un error
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
    @Operation(summary = "Save in system a reservation with the hotel passed in parameter")
    @PostMapping
    public ResponseEntity<ReservationResponse> post(@Valid @RequestBody ReservationRequest reservationRequest){
        return ResponseEntity.ok(reservationService.create(reservationRequest));
    }

    @Operation(summary = "Return a reservation with id passed")
    @GetMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.read(id));
    }

    @Operation(summary = "Update a reservation")
    @PutMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> put(@PathVariable UUID id, @Valid @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(reservationService.update(reservationRequest, id));
    }

    @Operation(summary = "Delete a reservation with id passed")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Return a reservation price given a hotel id")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getReservationPrice(@RequestParam Long hotelId) {
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        return ResponseEntity.ok(Collections.singletonMap("ReservationPrice", reservationService.findPrice(hotelId)));
    }
}
