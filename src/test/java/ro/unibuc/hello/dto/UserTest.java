package ro.unibuc.hello.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import ro.unibuc.hello.data.UserEntity;

public class UserTest {
    
    private static LocalDateTime before = LocalDateTime.now();
    private static UserEntity user = new UserEntity();
    private static LocalDateTime after = LocalDateTime.now();
    private static String uuid = UUID.randomUUID().toString();

    @Test
    public void test_content() {
        Assertions.assertDoesNotThrow(() -> UUID.fromString(user.getSessionId()));
        Assertions.assertNotNull(user.getSessionId());
        
        user.setSessionId(uuid);
        Assertions.assertEquals(uuid, user.getSessionId());


        Assertions.assertTrue(user.getCreatedAt().isAfter(before));
        Assertions.assertTrue(user.getCreatedAt().isBefore(after));

        Assertions.assertTrue(user.getLastActiveAt().isAfter(before));
        Assertions.assertTrue(user.getLastActiveAt().isBefore(after));
    }

    @Test
    public void test_last_active() {
        UserEntity user = new UserEntity();
        LocalDateTime newTime = LocalDateTime.of(2025, 1, 1, 12, 0);
        
        user.setLastActiveAt(newTime);

        Assertions.assertEquals(newTime, user.getLastActiveAt());
    }
}
