package be.amisdubonbourgogne.web.auth;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    /** Horloge qu'on fait avancer à la main */
    static class TestClock extends Clock {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advance(Duration d) { now = now.plus(d); }
    }

    private final TestClock clock = new TestClock();
    private final LoginAttemptService service = new LoginAttemptService(5, Duration.ofMinutes(15), clock);

    @Test
    void bloqueApresCinqEchecs() {
        for (int i = 0; i < 4; i++) service.recordFailure("1.2.3.4");
        assertThat(service.remainingBlock("1.2.3.4")).isZero();

        service.recordFailure("1.2.3.4");
        assertThat(service.remainingBlock("1.2.3.4")).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void neBloqueQueLAdresseConcernee() {
        for (int i = 0; i < 5; i++) service.recordFailure("1.2.3.4");
        assertThat(service.remainingBlock("5.6.7.8")).isZero();
    }

    @Test
    void debloqueQuandLesEchecsSortentDeLaFenetre() {
        for (int i = 0; i < 5; i++) service.recordFailure("1.2.3.4");
        clock.advance(Duration.ofMinutes(15).plusSeconds(1));
        assertThat(service.remainingBlock("1.2.3.4")).isZero();
    }

    @Test
    void uneConnexionReussieRemetLeCompteurAZero() {
        for (int i = 0; i < 4; i++) service.recordFailure("1.2.3.4");
        service.recordSuccess("1.2.3.4");
        service.recordFailure("1.2.3.4");
        assertThat(service.remainingBlock("1.2.3.4")).isZero();
    }
}
