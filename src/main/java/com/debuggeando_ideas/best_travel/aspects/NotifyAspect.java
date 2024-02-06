package com.debuggeando_ideas.best_travel.aspects;

import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import com.debuggeando_ideas.best_travel.util.annotations.Notify;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Esta clase representará a un "aspecto" y debe ser registrada en el "contenedor de spring"
 *
 * @Aspect
 * Anotación con la que trabajar spring framework para configurar un aspecto
 */
@Component
@Aspect
public class NotifyAspect {
    /*
     * Este método representa la ejecución de un aspecto
     * Debe ser obligatoriamente "void"
     *
     * JoinPoint
     * Representa un "punto de intercepción". Es manejado e inyectado por Spring framework
     * Es decir, que dentro de este método "notifyInFile()" se manejará lo interceptado por la anotaición "@Notify" en el método donde haya sido anotado.
     *
     * El método que ejecuta un aspecto tiene 3 posibles anotaciones:
     * 1) @Before : Se ejecutará el método "notifyInFile()" ANTES de que se ejecute el método anotado con "@Notify"
     * 2) @After : Se ejecutará el método "notifyInFile()" DESPUÉS de la ejecución del método anotado con "@Notify"
     * 3) @Around : Se ejecutará el método "notifyInFile()" DURANTE la ejecución del método anotado con "@Notify"
     *
     * value = "@annotation(com.debuggeando_ideas.best_travel.util.annotations.Notify)"
     * Indcamos la ruta de la anotación que se está utilizando para interceptar.
     */
    @After(value = "@annotation(com.debuggeando_ideas.best_travel.util.annotations.Notify)")
    public void notifyInFile(JoinPoint joinPoint) throws IOException {
        var args = joinPoint.getArgs();
        var size = args[1]; // segundo argumento del método "getAll()"
        var sortType = args[2] == null ? "NONE" : args[2]; // tercer argumento del método "getAll()"

        // obtenemos un objeto del tipo "" proveniente del "punto de intercepción"
        var signature = (MethodSignature) joinPoint.getSignature();
        // Obtenemos el método al que se ha anotado con "@Notify"
        var method = signature.getMethod();
        // Obtenemos la anotación para luego el valor del argumento "value="
        var annotation = method.getAnnotation(Notify.class);

        var text = String.format(LINE_FORMAT, LocalDateTime.now(), annotation.value(), size, sortType);

        BestTravelUtil.writeNotification(text, PATH);


        // Obtebemos los argumentos, con sus valores recbidos del request, correspondiente al método anotado con "@Notify".
        // En este caso del método "getAll()" de la clase "FlyController",
        // Luego los transformamos en un stream, iteramos e imprimimos.

        //Stream.of(joinPoint.getArgs()).forEach(System.out::println);
    }

    // Indcamos lo que vamos a escribir en el archivo de texto planoargs
    private static final String LINE_FORMAT = "At %s new request %s with size page %s and order %s";
    private static final String PATH = "files/notify.txt";
}
