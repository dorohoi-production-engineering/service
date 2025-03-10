package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserEntity {

    @Id
    private String id;

    private String anonymousId;
    private LocalDateTime createdAt;

    public UserEntity() {
        this.anonymousId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "UserEntity[id='%s', anonymousId='%s', createdAt='%s']",
                id, anonymousId, createdAt);
    }
}
