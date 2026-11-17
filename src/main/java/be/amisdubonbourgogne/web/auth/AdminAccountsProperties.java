package be.amisdubonbourgogne.web.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AdminAccountsProperties(List<Account> admins) {
    public AdminAccountsProperties {
        admins = admins == null ? List.of() : List.copyOf(admins);
    }

    public record Account(String username, String password) {
    }
}
