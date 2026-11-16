package be.amisdubonbourgogne.web.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Comptes admin lus dans la configuration :
 * <pre>
 * app:
 *   admins:
 *     - username: gerard
 *       password: "{bcrypt}$2a$10$…"
 * </pre>
 * Mot de passe en clair accepté en local ; chiffré en BCrypt recommandé (scripts/hash-password.sh).
 */
@ConfigurationProperties(prefix = "app")
public record AdminAccountsProperties(List<Account> admins) {

    public AdminAccountsProperties {
        admins = admins == null ? List.of() : List.copyOf(admins);
    }

    public record Account(String username, String password) {
    }
}
