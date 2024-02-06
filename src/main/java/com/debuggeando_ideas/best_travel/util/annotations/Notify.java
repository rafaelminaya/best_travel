package com.debuggeando_ideas.best_travel.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @Retention
 * Anotación que indica qué tipo de retención tendrá la anotación "Notify". Hay 3 tipos:  CLASS, RUNTIME, SOURCE
 *
 * RetentionPolicy.RUNTIME
 * indica que se ejecutará en tiempo de ejecución.
 *
 * @Target(ElementType.METHOD)
 * Indica que esta anotación se aplicará en un método.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Notify {
    /*
     *
     */
    // Este "value()" representa el argumento "value=" de diversas anotaciones.
    // indicaoms la ruta por defecto en caso on le enviemos algún valor al "value=" en el argumento de la anotación.
    String value() default "NONE";
}
