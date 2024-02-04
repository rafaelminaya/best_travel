package com.debuggeando_ideas.best_travel.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Value("${app.client.id}")
    private String clientId;
    @Value("${app.client.secret}")
    private String clientSecret;
    @Value("${app.client-scope-read}")
    private String scopeRead;
    @Value("${app.client-scope-write}")
    private String scopeWrite;
    @Value("${app.client-redirect-debugger}")
    private String redirectUri1;
    @Value("${app.client-redirect-spring-doc}")
    private String redirectUri2;

    private final UserDetailsService userDetailsService;

    // Inyección de dependencias por constructor manual para indicar cuál es la implementación que se va a utilizar.
    // Esto con el fin de evitar posibles errores ya que pueden haber varias implementaciones de "UserDetailsService".
    // "AppUserService" es la clase que implementa a la interfaz "UserDetailsService"
    public SecurityConfig(@Qualifier(value = "appUserService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Configuración del 1° Filtro de seguridad. "Servidor de autorización".
    // Este filtro también sirve para cuando falle el request (401 - unauthorized)
    @Bean
    @Order(1)
    public SecurityFilterChain SecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Este método realiza la configuración por defecto del "csrf" a nuestro argumento "httpSecurity". Este método lanza una excepción.
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        // Indicamos qué tipos de configuraciones usará.
        // Con "withDefaults()" indicamos que se usará la configuración por defecto del "csrf"
        httpSecurity
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        // Manejamos las excepciones, indicando la ruta del login a donde se redireccionará, representado por la constante "LOGIN_RESOURCE"
        httpSecurity
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_RESOURCE))
                );

        return httpSecurity.build();
    }

    // Configuración del 2° Filtro de seguridad. "Servidor de recursos"
    @Bean
    @Order(2)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .formLogin()
                .and()
                .authorizeHttpRequests()
                // Indicamos nuestras rutas publicas
                .requestMatchers(PUBLIC_RESOURCES).permitAll()
                // Rutas que requieren autenticación
                .requestMatchers(USER_RESOURCES).authenticated()
                // Rutas que requieren de autenticación y del rol ROLE_ADMIN ("granted_authorities" de cada documento de la colección "app_users")
                .requestMatchers(ADMIN_RESOURCES).hasAuthority(ROLE_ADMIN)
                .and()
                // Indicamos que el acceso al "servidor de recursos" será mediante un JWT
                .oauth2ResourceServer()
                .jwt();

        return httpSecurity.build();
    }

    // Método que configura cuál será nuestro "proveedor de autenticación". El argumento es una inyección de dependencias.
    // Este método será instanciado y almacenado en el "contenedor de spring" puesto que su argumento "BCryptPasswordEncoder",
    // ya es un bean del "contenedor de spring" (por estar anotado con "@Bean")
    // Por lo que sería opcional anotar este método con "@Autowired" y en su lugar usaremos "@Bean"
    @Bean
    public AuthenticationProvider authenticationProvider(BCryptPasswordEncoder encoder) {
        // Instancia del "proveedor de autenticación".
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Especificamos cual será el codificador y decodificador de passwords.
        authenticationProvider.setPasswordEncoder(encoder);
        // Indicamos de dónde vamos a obtener los datos del usuario de Spring Security.
        authenticationProvider.setUserDetailsService(this.userDetailsService);

        return authenticationProvider;
    }

    // Autenticación del cliente frontend. Recibe de argumento un encriptador ya que el contenido debe ser encriptado.
    @Bean
    public RegisteredClientRepository registeredClientRepository(BCryptPasswordEncoder encoder) {
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(this.clientId) // asignamos un "client id"
                .clientSecret(encoder.encode(this.clientSecret)) // asignamos el password del "client id"
                .scope(this.scopeRead)
                .scope(this.scopeWrite)
                .redirectUri(this.redirectUri1)
                .redirectUri(this.redirectUri2)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // indicamos que autenticación será con un "Basic Auth"
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // indicamos que nos autenticaremos por medio de un "código de autorización"
                .build();
        // Retornamos una clase que implementa la interfaz "RegisteredClientRepository" y cuyo argumento es del tipo "RegisteredClient"
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    // Configuración adicional del "servidor de autorización"
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración del decodificador del JWT.
    // El argumento del método requiere de haber registrado en el "contenededor de Spring" una implementación de la interfaz "JWKSource"
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // Registro de la implementación de la interfaz "JWKSource" en el "contenedor de spring".
    // Necesario para ser usado en el argumento del método "jwtDecoder()" de la clase actual.
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey(); // Método que genera una llave RSA, necesaria para generar el JWT
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // Genera automáticamente una llave RSA pública
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // Genera automáticamente una llave RSA privada
        // Genera una llave RSA para firmar el JWT y es almacenada en el backend
        // Para en el futuro validar que sea la firma correctode los JWT recibidos por el cliente.
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        // Genera un JWT a partir de la llave RSA
        JWKSet jwkSet = new JWKSet(rsaKey);
        // Se guarda el JWT como inmutable
        return new ImmutableJWKSet<>(jwkSet);
    }

    // Método que ayuda a generar una llave RSA para crear las RSA pública y privada.
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // indicmos el algoritmo
            keyPairGenerator.initialize(2048); // 2048 es un protocolo estandarizado
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    // Configuramos la duración del token en 8 horas
    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder().refreshTokenTimeToLive(Duration.ofHours(8)).build();
    }

    // Método que personaliza un token. Removeremos el prefijo de "SCOPE_" que trae el "authority" por defecto.
    // Necesita del métoodo "jwtAuthenticationConverter()", que está más abajo, para concretar la configuración.
    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        // Asignamos una cadena vacía ("") en reemplazo de la palabra "SCOPE_"
        converter.setAuthorityPrefix("");
        return converter;
    }

    // Método requerido para complementar el funcionamiento del método "jwtGrantedAuthoritiesConverter()" que personaliza un token.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    /*
     * OAuth2TokenCustomizer :
     * Interfaz funcional de Java.
     * Es genérica y debe recibir un argumento que extienda de "OAuth2TokenContext"
     * El generic a utilizar será la clase "JwtEncodingContext" que extiende de la interfaz "OAuth2TokenContext" que es lo que necesitamos.
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
        return context -> {
            // validacion ue sea el acces token
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                context.getClaims().claims(claim -> {
                    claim.putAll(Map.of(
                            "owner", APLICATION_OWNER,
                            "DATE_REQUEST", LocalDateTime.now().toString()
                    ));
                });
            }
        };
    }

    // Rutas públicas a cualquier visitante. Obtenido como [ROLE_ANONYMOUS] y se quedará así.
    private static final String[] PUBLIC_RESOURCES = {"/fly/**", "/hotel/**", "/swagger-ui/**", "/.well-known/**, ", "/v3/api-docs/**"};
    // Rutas privadas a nivel usuario: : Obtenido como [SCOPE_read] y modificado a "read"
    private static final String[] USER_RESOURCES = {"/ticket/**", "/reservation/**", "/tour/**"};
    // Rutas privadas a nivel administrador : Obtenido como [SCOPE_write]  y modificado a "write"
    private static final String[] ADMIN_RESOURCES = {"/user/**", "/report/**"};
    // Ruta públic al login
    private static final String LOGIN_RESOURCE = "/login";
    private static final String ROLE_ADMIN = "write";
    // Constante añadida como propiedad adicional en le "payload" del token
    private static final String APLICATION_OWNER = "Rafael Minaya";
}
