package com.debuggeando_ideas.best_travel.config;

import com.debuggeando_ideas.best_travel.util.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

// Clase para la configuracion del cache de spring al levantarse el proyecto
@Configuration
@EnableCaching // Autoconfigura el cache de Spring, en este caso con Redis
@EnableScheduling // Permite realizar tareas calendarizadas
@Slf4j
public class RedisConfig {

    //  Inyeccion de valores del archivo "redis.properties"
    @Value(value = "${cache.redis.address}")
    private String serverAddress;
    @Value(value = "${cache.redis.password}")
    private String serverPassword;

    // Interfaz que por dentro trabaja con la clase "RedisClient"
    @Bean
    public RedissonClient redissonClient() {
        // Variable de configuración, "Config" es propia de Spring Cache. Importada de "org.redisson.config;"
        var config = new Config();
        /*
          - Lo interesante de Redis, es que puede ser "clousterizado".
          - Es decir, tener múltiples máquinas, como un "clouster" trabajando con Redis,
            y en caso se caiga una instancia tenemos otras arriba.
          - Usaremos el método "useSingleServer()" por ser un ambiente local, pero usaríamos "useClusterServers()",
            para un ambiente en producción.
         */
        config.useSingleServer()
                // seteamos nuestras redenciales
                .setAddress(this.serverAddress)
                .setPassword(this.serverPassword);

        // Retornamos el tipo "RedissonClient" esperado por el método, usando el método "create()" de la clase "Redisson"
        return Redisson.create(config);
    }

    /*
      - Creamos el "cache manager" de Spring que se va a integrar con Redis.
      - El parámetro recibido por este métddo es necesario para integrar "Spring Cache" con Redis.
      - Con "@Autowired" forzaremos la inyección de dependencias a este método.
       * Ya que este método necesita que previamente su parámetro del tipo "RedissonClient" haya sido configurado y almacenado en el "contenedor de Spring".
       * Forzando que el método de arriba "redissonClient()" se lance automáticamente primero por el IOC de Spring y sea requerido por este método "cacheManager()".
       * Esto debido a que no podemos inyectar manualmente el método "redissonClient()" por ser un método "@Bean" dentro de una clase
       "@Configuration", debemos dejar que el IOC de Spring se encargue, de lo contario botará un error.
     */
    @Bean
    @Autowired
    public CacheManager cacheManager(RedissonClient redissonClient) {
        // Creamos una configuración
        var configs = Map.of(
                CacheConstants.FLY_CACHE_NAME, new CacheConfig(),
                CacheConstants.HOTEL_CACHE_NAME, new CacheConfig()
        );

        return new RedissonSpringCacheManager(redissonClient, configs);
    }

    /*
      - Método, que debe ser "void", encargado de limpiar el cache cada cierta hora, en este caso cada medianoche.
      - @Scheduled:
       * Permite calendarizar métodos, es decir, ser ejecutados cada cierto tiempo según el parámetro dado.
       * cron : Representa una "expresión crontab" para indicar cada cuánto tiempo se ejecutará el método.

      - @CacheEvict
       * Permite eliminar los "caches".
        * Recibiendo como parámetro el nombre de los caches a eliminar.

     -  allEntries = true
     * Confirma que todos los "caches" indicados serán eliminados.

     - @Async:
       * Permite ser ejecutado en un hilo aparte, siendo asíncrono.

     -NOTA: También es posible utilizar la anotación "@CacheEvict" con todos sus argumentos en los métodos de servicios,
     para que luego de ser ejecutados se elimine el cache, por ejemplo en los métodos "update()" o "delete()" del "ReservationService"
     */
    @CacheEvict(cacheNames = {
            CacheConstants.FLY_CACHE_NAME,
            CacheConstants.HOTEL_CACHE_NAME

    }, allEntries = true)
    @Scheduled(cron = CacheConstants.SCHEDULED_RESET_CACHE_MIDNIGHT)
    @Async
    public void deleteCache(){
        // Mensaje enviado al terminal cada vez que se efectúa la eliminación del cache.
        log.info("Clean cache");
    }

}
