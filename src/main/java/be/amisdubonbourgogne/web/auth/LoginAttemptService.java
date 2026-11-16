package be.amisdubonbourgogne.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limite les essais de connexion : au-delà de {@code maxFailures} échecs en {@code window}
 * depuis la même adresse IP, les tentatives sont refusées jusqu'à la fin de la fenêtre.
 * En mémoire : remis à zéro au redémarrage, suffisant pour un seul serveur.
 */
@Service
public class LoginAttemptService {
    private final int maxFailures;
    private final Duration window;
    private final Clock clock;
    private final Map<String, Deque<Instant>> failures = new ConcurrentHashMap<>();

    @Autowired // deux constructeurs : celui-ci est celui de Spring, l'autre sert aux tests
    public LoginAttemptService(@Value("${app.login.max-failures:5}") int maxFailures,
                               @Value("${app.login.window-minutes:15}") long windowMinutes) {
        this(maxFailures, Duration.ofMinutes(windowMinutes), Clock.systemUTC());
    }

    LoginAttemptService(int maxFailures, Duration window, Clock clock) {
        this.maxFailures = maxFailures;
        this.window = window;
        this.clock = clock;
    }

    /** Temps d'attente restant si cette adresse est bloquée, sinon Duration.ZERO. */
    public Duration remainingBlock(String ip) {
        Deque<Instant> attempts = failures.get(ip);
        if (attempts == null) {
            return Duration.ZERO;
        }
        synchronized (attempts) {
            prune(attempts);
            if (attempts.size() < maxFailures) {
                return Duration.ZERO;
            }
            return Duration.between(clock.instant(), attempts.peekFirst().plus(window));
        }
    }

    public void recordFailure(String ip) {
        Deque<Instant> attempts = failures.computeIfAbsent(ip, k -> new ArrayDeque<>());
        synchronized (attempts) {
            prune(attempts);
            attempts.addLast(clock.instant());
        }
    }

    public void recordSuccess(String ip) {
        failures.remove(ip);
    }

    private void prune(Deque<Instant> attempts) {
        Instant limit = clock.instant().minus(window);
        while (!attempts.isEmpty() && attempts.peekFirst().isBefore(limit)) {
            attempts.removeFirst();
        }
    }
}
