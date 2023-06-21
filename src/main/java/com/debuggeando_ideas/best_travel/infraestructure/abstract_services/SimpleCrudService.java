package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

/*
- Esta será la interfaz genérica para todos los CRUDs del proyecto(ticket, reservation, tour)
- RQ : Representa el tipo genérico del request
- RS : Representa el tipo genérico del response
- ID : Representa el tipo genérico del id
 */
public interface SimpleCrudService<RQ, RS, ID> {
    // Métodos para el Simple CRUD
    RS create(RQ request);
    RS read(ID id);
    void delete(ID id);
}
