package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.HotelResponse;
import com.debuggeando_ideas.best_travel.domain.entities.HotelEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IHotelService;
import com.debuggeando_ideas.best_travel.util.constants.CacheConstants;
import com.debuggeando_ideas.best_travel.util.enums.SortType;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
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
    /*
     - @Cacheable
     * Permite que Spring almacene y lea datos del cache.
     - Thread.sleep(7000);
     * La primera vez sí demorará los 7 segundos correspondiente al "Thread".
     * Ya que el cache estará vacío y primero se almacena y luego se lee.
     * Funcionando recién el "cache" a partir del segundo intento en el cual el "cache" ya tendrá datos
     - CacheConstants.HOTEL_CACHE_NAME
     * Clase y Constante personalizadas creadas en el paquete "util.constants"
     */

    @Override
    @Cacheable(value = CacheConstants.HOTEL_CACHE_NAME)
    public Set<HotelResponse> readLessPrice(BigDecimal price) {
        // Dormiremos por 7 segundos este método al ser invocado, para simiular una demora en el servidor
        // y así ver la diferencia al invocar el método con el uso de cache.
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return hotelRepository.findByPriceLessThan(price)
                .stream()
                .map(HotelEntity -> this.entityToResponse(HotelEntity))
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.HOTEL_CACHE_NAME)
    public Set<HotelResponse> readBetweenPrices(BigDecimal min, BigDecimal max) {
        // Dormiremos por 7 segundos este método al ser invocado.
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return hotelRepository.findByPriceBetween(min, max)
                .stream()
                .map(HotelEntity -> this.entityToResponse(HotelEntity))
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.HOTEL_CACHE_NAME)
    public Set<HotelResponse> readByRatingGreater(Integer rating) {
        // Dormiremos por 7 segundos este método al ser invocado.
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
