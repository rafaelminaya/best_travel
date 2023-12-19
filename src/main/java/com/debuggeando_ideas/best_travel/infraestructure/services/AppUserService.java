package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.domain.repositories.mongo.AppUserRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ModifyUserService;
import com.debuggeando_ideas.best_travel.util.exceptions.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AppUserService implements ModifyUserService {
    private final AppUserRepository appUserRepository;

    @Override
    public Map<String, Boolean> enabled(String username) {
        // Buscamos al usuario de la base de datos MongoDB.
        var userToUpdate = this.appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(COLLECTION_NAME));
        // Actualizamos el "enabled" al opuesto de lo obtenido.
        userToUpdate.setEnabled(!userToUpdate.isEnabled());
        // Persistimos el usuario en la base de datos.
        var userUpdated = this.appUserRepository.save(userToUpdate);
        // Retornamos un "Map" cuyo value es el "username" y el key el "enabled" del usuario persistido.
        return Collections.singletonMap(userUpdated.getUsername(), userUpdated.isEnabled());
    }

    @Override
    public Map<String, Set<String>> addRole(String username, String role) {
        // Buscamos al usuario de la base de datos MongoDB.
        var userToUpdate = this.appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(COLLECTION_NAME));
        // Añadimos un "granted_authorities" al usuario, obtenido del argumento.
        userToUpdate.getRole().getGrantedAuthorities().add(role);
        // Persistimos el usuario en la base de datos.
        var userUpdated = this.appUserRepository.save(userToUpdate);
        // Guardamos los "granted_authorities" en una variable para luego devolverla.
        var authorities = userToUpdate.getRole().getGrantedAuthorities();
        log.info("User {} add role {}", userToUpdate.getUsername(), userUpdated.getRole().getGrantedAuthorities());
        // Retornamos un "Map" cuyo value es el "username" y el key el "granted_authorities" del usuario persistido.
        return Collections.singletonMap(userUpdated.getUsername(), authorities);
    }

    @Override
    public Map<String, Set<String>> removeRole(String username, String role) {
        // Buscamos al usuario de la base de datos MongoDB.
        var userToUpdate = this.appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(COLLECTION_NAME));
        // Eliminamos un "granted_authorities" del usuario, obtenido del argumento.
        userToUpdate.getRole().getGrantedAuthorities().remove(role);
        // Persistimos el usuario en la base de datos.
        var userUpdated = this.appUserRepository.save(userToUpdate);
        // Guardamos los "granted_authorities" en una variable para luego devolverla.
        var authorities = userToUpdate.getRole().getGrantedAuthorities();
        log.info("User {} remove role {}", userToUpdate.getUsername(), userUpdated.getRole().getGrantedAuthorities());
        // Retornamos un "Map" cuyo value es el "username" y el key el "granted_authorities" del usuario persistido.
        return Collections.singletonMap(userUpdated.getUsername(), authorities);
    }

    @Transactional(readOnly = true)
    public void loadUserByUsername(String username) {
        // Buscamos al usuario de la base de datos MongoDB.
        var userFromDB = this.appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(COLLECTION_NAME));
    }

    // Constante correspondiente al nombre de la coleccion, en vez de añadirla al enum "Tables" o crear un nuevo enum para colecciones.
    private static final String COLLECTION_NAME = "app_users";
}
