package com.debuggeando_ideas.best_travel.util.exceptions;

// Excepción personlizada lanzada cada vez que un id buscado, enviado como argumento, no sea encontrado en la BD.
public class IdNotFoundException extends RuntimeException {
    // Mensaje de la excepción, que concatenará el nombre de la tabla de forma dinámica.
    // La "%s" para indicar que se reemplazará por un valor recibido de tipo "String".
    private static final String ERROR_MESSAGE = "Record no exist in %s";
    // Constructor
    public IdNotFoundException(String tableName){
        // Con "super()" invocamos al constructor del padre "RuntimeException",
        // enviandole la constante concatenándole el parámetro del constructor de la clase actual "IdNotFoundException"
        super(String.format(ERROR_MESSAGE, tableName));
    }
}
