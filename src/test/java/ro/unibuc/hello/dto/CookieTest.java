package ro.unibuc.hello.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ro.unibuc.hello.data.CookieEntity;

class CookieTest {
    
    private static String sessionId = "1";
    private static String userId = "1";
    private static LocalDateTime expiresAt = LocalDateTime.of(2025, 3, 24, 12, 0);;

    CookieEntity cookie = new CookieEntity(sessionId, userId, expiresAt);

    @Test
    void test_content() {
        Assertions.assertEquals(sessionId, cookie.getSessionId());
        Assertions.assertEquals(userId, cookie.getUserId());
        Assertions.assertEquals(expiresAt, cookie.getExpiresAt());

        LocalDateTime now = LocalDateTime.now();
        Duration tolerance = Duration.between(now, cookie.getCreatedAt()).abs();
        Assertions.assertTrue(tolerance.getSeconds() < 3);
    }
}
