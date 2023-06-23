package com.debuggeando_ideas.best_travel.api.controllers.error_handler;

import com.debuggeando_ideas.best_travel.api.models.reponses.BaseErrorResponse;
import com.debuggeando_ideas.best_travel.api.models.reponses.ErrorResponse;
import com.debuggeando_ideas.best_travel.util.exceptions.ForbiddenCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Controlador que manejará los status code 403 - Bad Request
@RestControllerAdvice
@ResponseStatus(HttpStatus.FORBIDDEN) // especifica que este controlador siempre lanzará el status 403 - Forbidden
public class ForbiddenCustomerHandler {
    @ExceptionHandler(ForbiddenCustomerException.class) // anotación que intercepta la excepción indicada
    public BaseErrorResponse handleForbidden(ForbiddenCustomerException exception) {
        // Retornarnamos una nueva instancia de "ErrorResponse", enviando a su constructor los valores de ambas clases.
        // "ErrorResponse" es el hijo de la clase "BaseErrorResponse" a retornar.
        return ErrorResponse.builder()
                .error(exception.getMessage()) // Mensaje de la excepción.
                .status(HttpStatus.FORBIDDEN.name()) // "FORBIDDEN"
                .code(HttpStatus.FORBIDDEN.value()) // 403
                .build();
    }
}
