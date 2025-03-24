package ro.unibuc.hello.data;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import ro.unibuc.hello.data.SubscriptionEntity;


@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByUserId(String userId);
}
