package main.java.ro.unibuc.hello.data;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity, String> {
}
