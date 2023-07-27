package com.debuggeando_ideas.best_travel.infraestructure.helpers;

import com.debuggeando_ideas.best_travel.infraestructure.dtos.CurrencyDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Currency;

// Clase que será un conectar que implementará nuestro "web client".
// No usaremos "lombok" ya que usaremos "@Value" y "@Qualifier" que tienen conflicto con "lombok"
@Component
public class ApiCurrencyConnectorHelper {
    // Inyectamos nuestro "web client", cuya implementación se encuentra en la clase "WebClientConfig"
    private final WebClient currencyWebClient;
    @Value(value = "${api.base-currency}")
    private String baseCurrency;

    // CONSTRUCTOR
    public ApiCurrencyConnectorHelper(@Qualifier(value = "currency") WebClient currencyWebClient) {
        this.currencyWebClient = currencyWebClient;
    }

    // constantes que construyen el endpoint.
    // Sería similar a : https://api.apilayer.com/exchangerates_data/latest?base=USD&symbols=MXN
    // Los valoresentre llaves serán reemplazados por los valores recibidos.
    private static final String CURRENCY_PATH = "/exchangerates_data/latest";
    private static final String BASE_CURRENCY_QUERY_PARAM = "?base={base}";
    private static final String SYMBOL_CURRENCY_QUERY_PARAM = "&symbols={symbols}";

    // Método que hal ser invocada hará el consumo el API
    public CurrencyDTO getCurrency(Currency currency) {
        // El tipo "WebClient" a retornar contiene los diferentes métodos http(get, post, put, delete, etc)
        return this.currencyWebClient
                .get() // Indicamos el método http "GET" a usar
                // Construimos el uri del endpoint
                .uri(uriBuilder ->
                        /*
                        - Primer bloque con el método "path()", luego añadiendo con "query()" y construimos con "build()"
                        - Cada argumento del "query()" será reemplazado por cada argumento del método "build()".
                        - Es decir:
                         * En BASE_CURRENCY_QUERY_PARAM, se reemplazará el "{base}" por el varlor de "this.baseCurrency"
                         * En SYMBOL_CURRENCY_QUERY_PARAM, se reemplazará el "{symbols}" por el valor de "currency.getCurrencyCode()"
                        - getCurrencyCode() : Método de la clase "Currency" que obtiene el código del país.
                         */
                        uriBuilder.path(CURRENCY_PATH)
                                .query(BASE_CURRENCY_QUERY_PARAM)
                                .query(SYMBOL_CURRENCY_QUERY_PARAM)
                                .build(this.baseCurrency, currency.getCurrencyCode())
                )
                // Ejecución del endpoint
                .retrieve()
                /*
                - Esto es propio de Spring Webflux, el cual trabaja con las clases wrapper, los stream reactivos.
                Los cuales tenemos : "flux" para una colección de datos y "mono" para datos singulare, el cual usaremos.
                - Indicamos que el response del API se va a mapear/convertir a una clase "CurrencyDTO"
                 */
                .bodyToMono(CurrencyDTO.class)
                // "block()" se utiliza cuando estamos usando Spring normal, si estuviésemos en spring reactivo tendríamos que usare otras alternativas.
                .block();
    }


}
