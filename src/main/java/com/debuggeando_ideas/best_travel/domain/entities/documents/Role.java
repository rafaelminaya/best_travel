package com.debuggeando_ideas.best_travel.domain.entities.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/*
 - El nombre de esta clase no se llama "RoleDocument" puesto que no es un documento como tal.
 - Sino más bien sería un objeto dentro de otro objeto, que representa el documento de "app_users" de MongoDB
 - Por lo no será anotado con "@Document" como sí lo fue la clase "AppUserDocument"
 - Esto puesto que la clase "AppUserDocumente" está mapeando y modelando, mientras esta clase "Role" solo está modelando.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Role {
    /*
     - @Field :
     * Es lo equivalente a "@Column" en JPA.
     * Para casos donde el atributo de Java, no se llame igual al campo/propiedad de la BBDD.
     * El "camel case" de JPA no funciona en MongoDB.
     * Por lo que si no ponemos el mismo nombre campo/propiedad de la colección, debemos usar la anotación "@Field"
     */
    @Field(name = "granted_authorities")
    private Set<String> grantedAuthorities;
}
