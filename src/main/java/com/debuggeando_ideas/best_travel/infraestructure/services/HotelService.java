package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.HotelResponse;
import com.debuggeando_ideas.best_travel.domain.entities.HotelEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IHotelService;
import com.debuggeando_ideas.best_travel.util.SortType;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class HotelService implements IHotelService {
    private final HotelRepository hotelRepository;

    @Override
    public Page<HotelResponse> readAll(Integer page, Integer size, SortType sortType) {
        PageRequest pageRequest = null;
        // Retorna la paginación según el parámetro "sortType" en cualera de sus 3 posibles valores
        switch (sortType) {
            case NONE -> pageRequest = PageRequest.of(page, size);
            case LOWER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).ascending());
            case UPPER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).descending());
        }
        // Convertiremos con "map()" cada "FlyEntity" obtenido por el método "findAll()" a "FlyResponse".
        // Esto es posible ya que se está obteniendo un "Page", que es una colección que hereda de "Iterable" y puede aplicarse el "map()" para transformar cada valor.
        return hotelRepository
                .findAll(pageRequest)
                .map(HotelEntity -> this.entityToResponse(HotelEntity));
    }

    @Override
    public Set<HotelResponse> readLessPrice(BigDecimal price) {
        return hotelRepository.findByPriceLessThan(price)
                .stream()
                .map(HotelEntity -> this.entityToResponse(HotelEntity))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<HotelResponse> readBetweenPrices(BigDecimal min, BigDecimal max) {
        return hotelRepository.findByPriceBetween(min, max)
                .stream()
                .map(HotelEntity -> this.entityToResponse(HotelEntity))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<HotelResponse> readByRatingGreater(Integer rating) {
        return hotelRepository.findByRatingGreaterThan(rating)
                .stream()
                .map(HotelEntity -> this.entityToResponse(HotelEntity))
                .collect(Collectors.toSet());
    }

    private HotelResponse entityToResponse(HotelEntity hotelEntity) {
        HotelResponse hotelResponse = new HotelResponse();
        BeanUtils.copyProperties(hotelEntity, hotelResponse);
        return hotelResponse;
    }
}
