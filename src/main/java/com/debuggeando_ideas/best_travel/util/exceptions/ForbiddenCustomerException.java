package com.debuggeando_ideas.best_travel.util.exceptions;
// Excepción personalizada lanzdda cuando un "customer" esté en una lista negra y no pueda hacer alguna transacción.
public class ForbiddenCustomerException extends RuntimeException {
    public ForbiddenCustomerException() {
        super("This customer is blocked.");
    }
}
