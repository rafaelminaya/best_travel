package com.debuggeando_ideas.best_travel.api.models.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

// Clase base que dará formato a las respuestas de error
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder // usamos esta anotación, en reemplazo del "@Builder" debido a que esta clase será heredada
public class BaseErrorResponse implements Serializable {
    private String status;
    private Integer code;
}
