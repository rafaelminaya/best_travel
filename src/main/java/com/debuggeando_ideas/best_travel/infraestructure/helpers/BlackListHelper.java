package com.debuggeando_ideas.best_travel.infraestructure.helpers;

import com.debuggeando_ideas.best_travel.util.exceptions.ForbiddenCustomerException;
import org.springframework.stereotype.Component;

// Lista negra de clientes que no pueden realizar transacciones.
// De intentarlo, obtendremos un error 403 - Forbidden
@Component
public class BlackListHelper {
    public void isInBlackListCustomer(String customerId){
        if(customerId.equalsIgnoreCase("VIKI771012HMCRG093")){
            throw new ForbiddenCustomerException();
        }
    }
}
