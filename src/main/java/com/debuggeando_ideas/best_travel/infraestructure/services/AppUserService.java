package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.domain.entities.documents.AppUserDocument;
import com.debuggeando_ideas.best_travel.domain.repositories.mongo.AppUserRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ModifyUserService;
import com.debuggeando_ideas.best_travel.util.exceptions.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
@Slf4j
@Transactional
@Service(value = "appUserService")
public class AppUserService implements ModifyUserService, UserDetailsService {

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

    // Método sobreescrito por la implementación de la interfaz "UserDetailsService"
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Buscamos al usuario de la base de datos MongoDB.
        var userFromDB = this.appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(COLLECTION_NAME));
        return mapUserToDetails(userFromDB);
    }

    // Método mapeador para construir un "UserDetails" propio de Spring Security a partir del "AppUserDocument" de MongoDB
    private static UserDetails mapUserToDetails(AppUserDocument appUserDocument) {
        // GrantedAuthority : Representa los permisos del usuario de Spring Security
        // Obtengo cada  role del documento, los transform a "SimpleGrantedAuthority" que es una implementación de GrantedAuthority y los almaceno en el "Set"
        Set<GrantedAuthority> authorities = appUserDocument
                .getRole()
                .getGrantedAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        // Obtenemos e imprimimos los "authority" de la ruta actual
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities from db: " + authorities);

        // Retornamos una nueva instancia de "User", el cual es una clase que implementa la interfaz "UserDetails".
        return new User(appUserDocument.getUsername(),
                appUserDocument.getPassword(),
                appUserDocument.isEnabled(), //Si esta deshabilitada(true), no dejará loguear
                true,
                true,
                true,
                authorities);
    }

    // Constante correspondiente al nombre de la colección, en vez de añadirla al enum "Tables" o crear un nuevo enum para colecciones.
    private static final String COLLECTION_NAME = "app_users";
}
