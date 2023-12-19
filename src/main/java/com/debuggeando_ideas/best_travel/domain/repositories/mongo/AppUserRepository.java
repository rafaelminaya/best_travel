package com.debuggeando_ideas.best_travel.domain.repositories.mongo;

import com.debuggeando_ideas.best_travel.domain.entities.documents.AppUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

// MongoRepository : Equivale al "CrudRepository" del JPA, pero para MongoDB
public interface AppUserRepository extends MongoRepository<AppUserDocument, UUID> {
    /*
     - En Mongo, NO podemos usar la anotación "@Query()" como en JPA.
     - Pero SÍ podemos usar los "Query Methods" como en JPA.
     - Este método equivale a:

     db.app_users.find({ username: "ragnar777" })

     */
    Optional<AppUserDocument> findByUsername(String username);
}
