package ro.unibuc.hello.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherDataEntity, String> {
        Optional<WeatherDataEntity> findByCity(String city);
}
