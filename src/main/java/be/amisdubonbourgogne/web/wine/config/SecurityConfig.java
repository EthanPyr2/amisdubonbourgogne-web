package be.amisdubonbourgogne.web.wine.config;

import be.amisdubonbourgogne.web.auth.AdminAccountsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AdminAccountsProperties.class)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                // Applique les règles de CorsConfig AVANT les contrôles de sécurité
                .cors(Customizer.withDefaults())
                // Session par cookie : il faut une protection CSRF. Mode « SPA » : le jeton est publié dans
                // le cookie XSRF-TOKEN, qu'Angular renvoie automatiquement dans l'en-tête X-XSRF-TOKEN.
                .csrf(csrf -> csrf.spa())
                // L'utilisateur connecté est gardé dans la session HTTP (cookie JSESSIONID, HttpOnly)
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        // Vérifications CORS (preflight) envoyées par le navigateur
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Page d'erreur de Spring Boot : sans ça, une erreur 400/404 devient un 403
                        .requestMatchers("/error").permitAll()
                        // Site public : lecture seule
                        .requestMatchers(HttpMethod.GET, "/api/wines/**", "/api/regions/**").permitAll()
                        // Connexion : ouvert à tous (la route /me répond 401 si personne n'est connecté)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Gestion de la carte : connexion obligatoire
                        .requestMatchers("/api/admin/**").authenticated()
                        // Tout le reste est refusé
                        .anyRequest().denyAll()
                )
                // Non connecté : simple 401 (sans en-tête WWW-Authenticate, donc sans fenêtre de login du navigateur)
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // POST /api/auth/logout : vide la session et répond 204
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));

        return http.build();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * Comptes admin : la liste app.admins (plusieurs comptes) ou, à défaut, le compte unique
     * spring.security.user.name / password. Tous ont les mêmes droits.
     * Mot de passe en clair (local) ou chiffré « {bcrypt}$2a$10$… » (recommandé en production).
     * L'application refuse de démarrer si aucun compte valable n'est configuré.
     */
    @Bean
    UserDetailsService adminAccounts(AdminAccountsProperties properties,
                                     @Value("${spring.security.user.name:}") String fallbackName,
                                     @Value("${spring.security.user.password:}") String fallbackPassword) {
        List<AdminAccountsProperties.Account> accounts = properties.admins().isEmpty()
                ? List.of(new AdminAccountsProperties.Account(fallbackName, fallbackPassword))
                : properties.admins();

        Set<String> usernames = new HashSet<>();
        List<UserDetails> users = accounts.stream().map(account -> {
            String username = account.username() == null ? "" : account.username().trim();
            String password = account.password() == null ? "" : account.password();
            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Compte admin incomplet : chaque compte doit avoir un username et un password (app.admins)");
            }
            if (!usernames.add(username.toLowerCase())) {
                throw new IllegalStateException("Compte admin en double : « " + username + " »");
            }
            String encoded = password.matches("^\\{.+}.*") ? password : "{noop}" + password;
            return User.withUsername(username).password(encoded).roles("ADMIN").build();
        }).toList();

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService adminAccounts) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminAccounts);
        provider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        return new ProviderManager(provider);
    }
}
