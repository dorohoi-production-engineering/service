package ro.unibuc.hello.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CookieRepository extends MongoRepository<CookieEntity, String> {
    Optional<CookieEntity> findBySessionId(String sessionId);
    Optional<CookieEntity> findByUserId(String userId);
}