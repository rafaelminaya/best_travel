package com.debuggeando_ideas.best_travel.api.controllers.error_handler;

import com.debuggeando_ideas.best_travel.api.models.reponses.BaseErrorResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

/*
 - Controlador para manejar todas los métodos, cuyo status code a devolver es 400 - Bad Request
 - @ExceptionHandler() :
  * Anotación que utiliza un aspecto, es decir, Spring AOP.
  * Interceptará todas las clases de excepción lanzadas que se indiquen dentro del argumento.
  * Ejemplo: "IdNotFoundException", "MethodArgumentNotValidException"
 */
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST) // especifica que este controlador siempre lanzará el status 400 - Bad Request
public class BadRequestController {

    // Método que se invocará al lanzarse una excepción "IdNotFoundException".
    // Es decir, un id buscado que no sea encuentre en la BD.
    @ExceptionHandler(IdNotFoundException.class) // anotación que intercepta la excepción indicada
    public BaseErrorResponse handleIdNotFound(IdNotFoundException exception) {
        // Devolvemos una instancia de "ErrorResponse", con los atributos para la clase padre e hijo
        // "ErrorResponse" es hijo de la clase "BaseErrorResponse" a retornar.
        return ErrorResponse.builder()
                .error(exception.getMessage()) // asignamos el mensaje de la excepción.
                .status(HttpStatus.BAD_REQUEST.name()) // "BAD_REQUEST"
                .code(HttpStatus.BAD_REQUEST.value()) // 400
                .build();
    }

    /* - Método que se invocará al lanzarse una excepción "MethodArgumentNotValidException".
       - Es decir, de las anotaciones : @Size, @NotBlank, @NotNull, etc. pertenecientes a los requests.
       - Estas pertenecen al package "jakarta.validation.constraints.*" de la dependencia "spring-boot-starter-validation".
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // anotación que intercepta la excepción indicada
    public BaseErrorResponse handleArguments(MethodArgumentNotValidException exception) {
        // listado que contendrá los errores obtenidos de la excepción "MethodArgumentNotValidException"
        var errors = new ArrayList<String>();
        // Obtenemos todos los errores y los iteramos
        exception.getAllErrors() // Obtenemos todos los errores
                .forEach(error -> errors.add(error.getDefaultMessage())); // iteramos y agregamos cada error al listado

        // Devolvemos una instancia de "ErrorsResponse" que contiene una lista de errores.
        // "ErrorsResponse" es hijo de la clase "BaseErrorResponse" a retornar.
        return ErrorsResponse.builder()
                .errors(errors) // asignamos la lista de errores
                .status(HttpStatus.BAD_REQUEST.name()) // "BAD_REQUEST"
                .code(HttpStatus.BAD_REQUEST.value()) // 400
                .build();
    }
}
