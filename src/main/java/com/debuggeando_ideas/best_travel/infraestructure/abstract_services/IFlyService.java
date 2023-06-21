package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.reponses.FlyResponse;

import java.util.Set;

public interface IFlyService extends CatalogService<FlyResponse> {
    // Método para buscar un vuelo que coincida con un origen y destino dado.
    Set<FlyResponse> readByOriginDestiny(String origen, String destiny);
}
