package be.amisdubonbourgogne.web.wine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Applique les règles de CorsConfig AVANT les contrôles de sécurité
                .cors(Customizer.withDefaults())
                // API REST appelée par le front : pas de formulaire HTML, donc pas de CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Vérifications CORS (preflight) envoyées par le navigateur
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Page d'erreur de Spring Boot : sans ça, une erreur 400/404 devient un 403
                        .requestMatchers("/error").permitAll()
                        // Site public : lecture seule
                        .requestMatchers(HttpMethod.GET, "/api/wines/**", "/api/regions/**").permitAll()
                        // Gestion de la carte : authentification obligatoire
                        .requestMatchers("/api/admin/**").authenticated()
                        // Tout le reste est refusé
                        .anyRequest().denyAll()
                )
                // TODO : provisoire. Remplacer par un vrai login avant la mise en ligne.
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
