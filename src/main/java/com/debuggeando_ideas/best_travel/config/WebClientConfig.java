package com.debuggeando_ideas.best_travel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Esta clase configurará los valores para el uso de un "web client"
// @Configuration: Permite cargar el archivo y registrar los métodos anotados con "@Bean" al "contenedor de spring"
@Configuration
public class WebClientConfig {
    // Con "@Value" hacemos una inyección de dependencias para darle los valores desde un archivo ".properties".
    @Value(value = "${api.base.url}")
    private String baseUrl;
    @Value(value = "${api.api-key}")
    private String apiKey;
    @Value(value = "${api.api-key.header}")
    private String apiKeyHeader;

    /*
    - Creamos un "web client" que utilizaremos para el API de Currency.
    - Anotamos con "@Bean" para registrarlo en el "contenedor de spring" y luego poder inyectarlo como dependencia.
    - La interfaz "WebClient", por la instalación de la dependencia de "WebFlux", podremos importarla
    de "org.springframework.web.reactive.function.client.WebClient;"
     */
    @Bean(name = "currency")
//    @Primary
    public WebClient currencyWebClient() {
        //return con la configuración base para el "web client".
        // Solo usaremos el "web client" para el api de currency.
        // Este método ".builder()" no es de lombok, sino propio del "WebClient"
        return WebClient
                .builder()
                .baseUrl(this.baseUrl) // asignamos la url para el "web client"
                .defaultHeader(this.apiKeyHeader, this.apiKey) // asignamos el header y su valor para el "web client"
                .build();
    }

    // Segundo "web client"
    @Bean(name = "base")
    public WebClient baseWebClient() {
        return WebClient
                .builder()
                .baseUrl(this.baseUrl) // asignamos la url para el "web client"
                .defaultHeader(this.apiKeyHeader, this.apiKey) // asignamos el header y su valor para el "web client"
                .build();
    }
}
