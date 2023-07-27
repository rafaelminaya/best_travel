package com.debuggeando_ideas.best_travel.infraestructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;

// Clase DTO que contendrá la respuesta del API currency
@Data
public class CurrencyDTO implements Serializable {
    /*
    - @JsonProperty(value = "date")
     * Indica que recibirá en JSON una propiedad llamada "date".
     * Y dicha propiedad deberá ser mapeada a este atributo en java llamado "exchangeDate"
     */
    @JsonProperty(value = "date")
    private LocalDate exchangeDate;
    // Este atributo representará los valores dinámicos obtenidos en JSON. Ejemplo: "rates": { "MXN": 16.86485, "COP": 3949.19, "EUR": 0.90266 }
    // Currency : Clase propia de Java que contiene todos los códigos ISO de las monedas de cada país. Nos evitaría crear numberadores
    private Map<Currency, BigDecimal> rates;

}
