package com.debuggeando_ideas.best_travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/*
- Esta clase permitirá que podamos leer los parámetros de un archivo ".properties", por ejemplo usando "@Value".
En este caso del archivo "api_currency.properties"

@PropertySources
- Permite tener un arreglo de "@PropertySource". Estos leen archivos de configuración ".properties"
- En caso necesitemos leer un solo archivo ".properties", reemplazaríamos el "@PropertySources" por "@PropertySource".

@PropertySource
 - Permite leer los parámetros del archivo ".properties" indicado en el argumento.
 - classpath:configs/api_currecnty.properties : Equivale a la ruta "resources/configs/api_currecnty.properties"
 - Así que usaremos los paráemetros del archivo "api_currecnty.properties"
 - classpath : Indica que empezará a leer a partir de la carpeta "src"
 - Lo mismo es para la ruta "classpath:configs/redis.properties"
 */
@Configuration
//@PropertySource(value = "classpath:configs/api_currency.properties") // indicamos de qué archivo de "properties" leeremos los parámetros
@PropertySources({
        @PropertySource(value = "classpath:configs/api_currency.properties"),
        @PropertySource(value = "classpath:configs/redis.properties")
})
public class PropertiesConfig {
}
