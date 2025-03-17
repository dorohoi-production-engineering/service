package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserEntity {

    @Id
    private String id;

    private String sessionId;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;

    public UserEntity() {
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setLastActiveAt(LocalDateTime time) {
        this.lastActiveAt = time;
    }

    public LocalDateTime getLastActiveAt() {
        return this.lastActiveAt;
    }

    @Override
    public String toString() {
        return String.format(
                "UserEntity[id='%s', sessionId='%s', createdAt='%s', lastActiveAt='%s']",
                id, sessionId, createdAt, lastActiveAt);
    }
}
