package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.util.enums.SortType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Set;

/*
 - Esta será la interfaz genérica para todos los catálogos.
 - RS : Representa el tipo genérico del response.
 */
public interface CatalogService<RS> {
    /* Retornaremos los resultados paginados
    - page : Número de la página a mostrar
    - size : Cantidad de elementos por página
    - SortType : Enumerador creado por nosotros.
    - sortType : Ordenamieto los elementos en cada página
     */
    Page<RS> readAll(Integer page, Integer size, SortType sortType);

    // listado de elementos cuyo precio sea menor al indicado
    Set<RS> readLessPrice(BigDecimal price);

    // listado de elementos cuyo precio se encuentre entre el rango de precios dado (min y max)
    Set<RS> readBetweenPrices(BigDecimal min, BigDecimal max);

    //Indica según qué atributo de la clase será ordenado.
    // Será "price", ya que es un atributo en común en las clases "FlyEntity" y "HotelEntity"
    // Y poder utilizar esta misma interfaz "CatalogService" para paginar en ambas clases "FlyEntity" y "HotelEntity"
    String FIELD_BY_SORT = "price";
}
