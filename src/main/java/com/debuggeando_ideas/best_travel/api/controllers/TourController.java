package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.TourResponse;
import com.debuggeando_ideas.best_travel.api.models.requests.TourRequest;
import com.debuggeando_ideas.best_travel.infraestructure.services.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(path = "tour")
@Tag(name = "Tour")
// Anotación de OpenApi-Swagger para modificar el subtítulo de este controlador en la interfaz gráfica.
public class TourController {
    private final TourService tourService;

    /*
    - @ApiResponse :
    * Anotación de OpenApi-Swagger para indicar qué devolver al obtener un error
    - content :
    * Añade los "Schema" en la interfaz gráfica de swagger correspondiente a los DTOs response de error. (ErrorResponse, ErrorsResponse)
    - @Operation :
    * Anotación propia de OpenApi-Swagger para añadir descripción de este controlador en la interfaz gráfica.
     */
    @ApiResponse(
            responseCode = "400",
            description = "When the request have a field invalid we response this",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @Operation(summary = "Save in system a tour based in list of hotels and flights")
    @PostMapping
    public ResponseEntity<TourResponse> post(@Valid @RequestBody TourRequest tourRequest) {
        System.out.println(tourService.getClass().getSimpleName());
//        return ResponseEntity.ok(tourService.create(tourRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.create(tourRequest));
    }

    @Operation(summary = "Return a Tour with id passed")
    @GetMapping(path = "{id}")
    public ResponseEntity<TourResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.read(id));
    }

    @Operation(summary = "Delete a Tour with id passed")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a ticket from tour")
    @PatchMapping(path = "{tourId}/add_ticket/{flyId}")
    public ResponseEntity<Map<String, UUID>> postTicket(@PathVariable Long tourId, @PathVariable Long flyId) {
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        var response = Collections.singletonMap("ticketId", tourService.addTicket(tourId, flyId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a ticket from tour")
    @PatchMapping(path = "{tourId}/remove_ticket/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long tourId, @PathVariable UUID ticketId) {
        tourService.removeTicket(tourId, ticketId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a reservation from tour")
    @PatchMapping(path = "{tourId}/add_reservation/{hotelId}")
    public ResponseEntity<Map<String, UUID>> postReservation(
            @PathVariable Long tourId,
            @PathVariable Long hotelId,
            @RequestParam Integer totalDays) {
        // singletonMap : método que permite enviar un "Map", de un solo elemento, indicando su key-value
        var response = Collections.singletonMap("ticketId", tourService.addReservation(tourId, hotelId, totalDays));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a reservation from tour")
    @PatchMapping(path = "{tourId}/remove_reservation/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long tourId, @PathVariable UUID reservationId) {
        tourService.removeReservation(tourId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
