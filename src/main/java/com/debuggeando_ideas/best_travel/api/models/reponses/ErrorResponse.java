package com.debuggeando_ideas.best_travel.api.models.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
/*
@EqualsAndHashCode
- Generado al pasar el cursor por la alerta en "@Data".
- Para indicar que el "equals()" y "hasCode()" sean manejados por la clase clase padre "BaseErrorResponse"
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder // Estamos esta anotación, en reemplazo del "@Builder" debido a que es clase abstracta
public class ErrorResponse extends BaseErrorResponse {
    private String error;
}
