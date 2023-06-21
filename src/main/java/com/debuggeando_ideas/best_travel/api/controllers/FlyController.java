package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.reponses.FlyResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IFlyService;
import com.debuggeando_ideas.best_travel.util.SortType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "fly")
public class FlyController {

    private final IFlyService flyService;

    /*
    - @RequestHeader: Indica que vendrá de la cabecera
    - (required = false) :  Indica que será un parámetro opcional,por defecto es "true"
     */
    @GetMapping
    public ResponseEntity<Page<FlyResponse>> getAll(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestHeader(required = false) SortType sortType) {
        // Asignamos el enumerador "NONE" en caso no se reciba el parámetro "sortType" (LOWER, UPPPER, NONE)
        if (Objects.isNull(sortType)) {
            sortType = SortType.NONE;
        }
        // Obtenemos la información paginada
        var response = flyService.readAll(page, size, sortType);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados de la paginación o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "less_price")
    public ResponseEntity<Set<FlyResponse>> getLessPrice(
            @RequestParam BigDecimal price) {
        // Obtenemos el listado
        var response = flyService.readLessPrice(price);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados de la paginación o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "between_price")
    public ResponseEntity<Set<FlyResponse>> getBetweenPrice(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        // Obtenemos el listado
        var response = flyService.readBetweenPrices(min, max);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    @GetMapping(path = "origin_destiny")
    public ResponseEntity<Set<FlyResponse>> getByOriginDestiny(
            @RequestParam String origin,
            @RequestParam String destiny) {
        // Obtenemos el listado
        var response = flyService.readByOriginDestiny(origin, destiny);
        // Operador ternario para contorlar lo retornado en caso no tengamos resultados o en caso sí tengamos resultados.
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }
}
