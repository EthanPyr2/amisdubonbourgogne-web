package be.amisdubonbourgogne.web.auth;

import be.amisdubonbourgogne.web.auth.dto.AuthResponse;
import be.amisdubonbourgogne.web.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/** Connexion à l'admin. La déconnexion (POST /api/auth/logout) est gérée par Spring Security. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final LoginAttemptService loginAttempts;

    /**
     * Qui est connecté ? 200 { username } ou 401.
     * Lire le jeton CSRF force aussi l'envoi du cookie XSRF-TOKEN, nécessaire avant tout POST (dont le login).
     */
    @GetMapping("/me")
    public AuthResponse me(Authentication authentication, CsrfToken csrfToken) {
        csrfToken.getToken();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new AuthResponse(authentication.getName());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
        // Trop d'échecs récents depuis cette adresse : refus immédiat, sans vérifier le mot de passe
        String ip = request.getRemoteAddr();
        Duration blocked = loginAttempts.remainingBlock(ip);
        if (!blocked.isZero() && !blocked.isNegative()) {
            long minutes = Math.max(1, (blocked.toSeconds() + 59) / 60);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Trop de tentatives de connexion. Réessayez dans " + minutes + " minute" + (minutes > 1 ? "s" : "") + ".");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(login.username(), login.password()));
        } catch (AuthenticationException e) {
            loginAttempts.recordFailure(ip);
            // Même message que l'identifiant ou le mot de passe soit faux : ne rien révéler
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiant ou mot de passe incorrect");
        }
        loginAttempts.recordSuccess(ip);
        // Nouvel identifiant de session à la connexion (protection contre la fixation de session)
        request.getSession(true);
        request.changeSessionId();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        return new AuthResponse(authentication.getName());
    }
}
