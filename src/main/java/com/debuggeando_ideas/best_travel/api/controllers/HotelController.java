package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.HotelResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IHotelService;
import com.debuggeando_ideas.best_travel.util.enums.SortType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "hotel")
public class HotelController {
    private final IHotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelResponse>> getAll(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestHeader(required = false) SortType sortType) {
        // Asignamos el enumerador "NONE" en caso no se reciba el parámetro "sortType" (LOWER, UPPPER, NONE)
        if (Objects.isNull(sortType)) {
            sortType = SortType.NONE;
        }
        // Obtenemos la información paginada
        var response = hotelService.readAll(page, size, sortType);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados de la paginación o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "less_price")
    public ResponseEntity<Set<HotelResponse>> getLessPrice(
            @RequestParam BigDecimal price) {
        // Obtenemos el listado
        var response = hotelService.readLessPrice(price);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados de la paginación o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "between_price")
    public ResponseEntity<Set<HotelResponse>> getBetweenPrice(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        // Obtenemos el listado
        var response = hotelService.readBetweenPrices(min, max);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "rating_greater")
    public ResponseEntity<Set<HotelResponse>> getByRatingGreater(
            @RequestParam Integer rating) {
        // validamos para que no se pase de los rangos de 1 a 5 estrellas
        if (rating > 4) rating = 4;
        if (rating < 1) rating = 1;
        // Obtenemos el listado
        var response = hotelService.readByRatingGreater(rating);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

}
