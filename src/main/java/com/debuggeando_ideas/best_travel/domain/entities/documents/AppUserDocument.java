package com.debuggeando_ideas.best_travel.domain.entities.documents;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

// Anotación equivalente a "@Entity" con "@Table" para mapear un documento en MongoDB, en el argumento se indica el nombre del documento.
@Document(collection = "app_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AppUserDocument implements Serializable {
    /*
    - El "_id" los documentos de MongoDB se generan automáticamente, y lo modelaremos como un tipo "String"
    - No usar el tipo de dato "UUID" para el id, ya que produciría un error de serialización.
     */
    @Id
    private String id;
    private String username;
    private String dni;
    /*
      - Usaremos el objeto primitivo "boolean" en vez de "Boolean".
      - Ya que por defecto tiene el valor "false", a diferencia del "Boolean" que por defecto es "null".
      - Evitando así la excepción de "NullPointerException".
     */
    private boolean enabled;
    private String password;
    private Role role;
}
