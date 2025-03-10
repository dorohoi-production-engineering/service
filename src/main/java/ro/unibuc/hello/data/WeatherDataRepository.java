package main.java.ro.unibuc.hello.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherDataEntity, String> {
    
}
