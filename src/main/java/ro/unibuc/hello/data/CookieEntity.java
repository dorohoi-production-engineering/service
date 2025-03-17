package ro.unibuc.hello.data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public class CookieEntity {
    
    @Id
    private String id;

    private String sessionId;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public CookieEntity(String sessionId, String userId, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}