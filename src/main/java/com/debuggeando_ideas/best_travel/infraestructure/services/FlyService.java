package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.reponses.FlyResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.FlyEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.FlyRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IFlyService;
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
public class FlyService implements IFlyService {
    private final FlyRepository flyRepository;

    @Override
    public Page<FlyResponse> readAll(Integer page, Integer size, SortType sortType) {
        PageRequest pageRequest = null;
        // Retorna la paginación según el parámetro "sortType" en cualquiera de sus 3 posibles valores
        switch (sortType) {
            case NONE -> pageRequest = PageRequest.of(page, size);
            case LOWER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).ascending());
            case UPPER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).descending());
        }
        // Convertiremos con "map()" cada "FlyEntity" obtenido por el método "findAll()" a "FlyResponse".
        // Esto es posible ya que se está obteniendo un "Page", que es una colección que hereda de "Iterable" y puede aplicarse el "map()" para transformar cada valor.
        return flyRepository
                .findAll(pageRequest)
                .map(flyEntity -> this.entityToResponse(flyEntity));
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readLessPrice(BigDecimal price) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return flyRepository.selectLessPrice(price)
                .stream()
                .map(flyEntity -> this.entityToResponse(flyEntity))
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readBetweenPrices(BigDecimal min, BigDecimal max) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return flyRepository.selectBetweenPrice(min, max)
                .stream()
                .map(flyEntity -> this.entityToResponse(flyEntity))
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readByOriginDestiny(String origen, String destiny) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return flyRepository.selectOriginDestiny(origen, destiny)
                .stream()
                .map(flyEntity -> this.entityToResponse(flyEntity))
                .collect(Collectors.toSet());
    }

    private FlyResponse entityToResponse(FlyEntity flyEntity) {
        FlyResponse flyResponse = new FlyResponse();
        BeanUtils.copyProperties(flyEntity, flyResponse);
        return flyResponse;
    }
}
