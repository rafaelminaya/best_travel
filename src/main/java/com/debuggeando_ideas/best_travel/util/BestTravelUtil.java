package com.debuggeando_ideas.best_travel.util;

import java.time.LocalDateTime;
import java.util.Random;

// Es recomendable que los métodos de la clase "utilieria" sean estáticos
public class BestTravelUtil {
    private static final Random random = new Random();

    // Fecha pronta, es decir, una fecha no muy alejada a la actual
    public static LocalDateTime getRandomSoon() {
        /*
        - Obtenemos las Horas a añadir a la fecha.
        - nextInt() : Obtiene un número aleatorio. En este caso un intervalo entre los números 2 y 5.
         5 : Número mayor del intervalo.
         -2: Número menor del intervalo.
         +2 : Número menor del intervalo.
         */
        var randomHours = random.nextInt(5 - 2) + 2;
        // Fecha actual
        var now = LocalDateTime.now();
        // Añadimos las horas a la fecha
        return now.plusHours(randomHours);
    }

    // Fecha un poco alejada a la actual
    public static LocalDateTime getRandomLater() {
        // Obtenemos las Horas a añadir a la fecha.
        var randomHours = random.nextInt(12 - 6) + 6;
        // Fecha actual
        var now = LocalDateTime.now();
        // Añadimos las horas a la fecha
        return now.plusHours(randomHours);
    }
}
