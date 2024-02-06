package com.debuggeando_ideas.best_travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/*
- Esta clase permitirá que podamos leer los parámetros de uno o varios archivos ".properties", por ejemplo usando "@Value".
 En este caso de los archivo de configuración: "api_currency.properties", "redis.properties", "client_security.properties"

@PropertySources
- Permite tener un arreglo de "@PropertySource". Estos leen archivos de configuración ".properties"
- En caso que necesitemos leer un solo archivo ".properties", reemplazaríamos el "@PropertySources" por "@PropertySource".

@PropertySource
 - Permite leer los parámetros del archivo ".properties" indicado en el argumento.
 - classpath:configs/api_currency.properties : Equivale a la ruta "resources/configs/api_currecnty.properties"
 - Así que usaremos los parámetros del archivo "api_currecnty.properties"
 - classpath : Indica que empezará a leer a partir de la carpeta "src"
 - Lo mismo es para la ruta "classpath:configs/redis.properties" y para "client_security.properties"
 */
@Configuration
//@PropertySource(value = "classpath:configs/api_currency.properties") // indicamos de qué archivo de "properties" leeremos los parámetros
@PropertySources({
        @PropertySource(value = "classpath:configs/api_currency.properties"),
        @PropertySource(value = "classpath:configs/redis.properties"),
        @PropertySource(value = "classpath:configs/client_security.properties")
})
public class PropertiesConfig {
}
