package com.debuggeando_ideas.best_travel.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/*
 * @Configuration
 * Toda clase con esta anotación, serán clases cargadas inmediatamente al levantarse el proyecto.
 * Estas clases generalmente cargan "bean" explícitos.
 * Es decir, métodos anotados con "@Bean", que retornan una instancia para ser almacenados en el "contenedor de Spring"
 *
 * @OpenAPIDefinition
 * Anotación propia de OpenApi, siendo sufiente para poder levantar la aplicación.
 * La información dentro del argumento es opcional, sirviendo para dar más información a la documentación.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Best Travel API",
                version = "1.0",
                description = "Documentación for endpoints in Best Travel"))
public class OpenApiConfig {

}
