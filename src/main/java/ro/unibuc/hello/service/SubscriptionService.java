package ro.unibuc.hello.service;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.dto.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private WeatherService weatherService;

    public List<WeatherDataEntity> getAllCitiesForUser(String id) throws EntityNotFoundException {
        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findById(id);
        SubscriptionEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        return entity.getCities() != null ? entity.getCities() : List.of();
    }

    public SubscriptionEntity createSubscription(String id) {
        SubscriptionEntity newSubscription = new SubscriptionEntity(id, List.of());
        return subscriptionRepository.save(newSubscription);
    }

    public void deleteSubscription(String id) throws EntityNotFoundException {
        SubscriptionEntity subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        subscriptionRepository.delete(subscription);
    }

    public CompletableFuture<SubscriptionEntity> addCityToSubscription(String id, String city) {
        return weatherService.test(city)
                .thenApply(weatherData -> {
                    WeatherDataEntity weatherDataEntity = new WeatherDataEntity(weatherData.getCity(), 
                            weatherData.getTemperature(), weatherData.getCondition(),
                            weatherData.getWindSpeed(), weatherData.getWindDirection(),
                            weatherData.getPrecipitations(), weatherData.getHumidity());

                    Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findById(id);
                    SubscriptionEntity subscriptionEntity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));

                    List<WeatherDataEntity> cities = subscriptionEntity.getCities();
                    cities.add(weatherDataEntity);
                    subscriptionEntity.setCities(cities);

                    return subscriptionRepository.save(subscriptionEntity);
                });
    }
    
    public CompletableFuture<SubscriptionEntity> deleteCityFromSubscription(String id, String cityName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findById(id);
            SubscriptionEntity subscriptionEntity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));

            List<WeatherDataEntity> cities = subscriptionEntity.getCities()
                    .stream()
                    .filter(city -> !city.getCity().equalsIgnoreCase(cityName))
                    .collect(Collectors.toList());

            subscriptionEntity.setCities(cities);

            return subscriptionRepository.save(subscriptionEntity);
        });
    }
}
