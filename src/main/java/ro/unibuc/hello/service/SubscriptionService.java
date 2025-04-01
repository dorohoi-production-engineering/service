package ro.unibuc.hello.service;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.dto.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
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
        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findByUserId(id);
        SubscriptionEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        return entity.getCities() != null ? entity.getCities() : List.of();
    }

    public SubscriptionEntity createSubscription(String id) {
        SubscriptionEntity newSubscription = new SubscriptionEntity(id, List.of(), List.of());
        return subscriptionRepository.save(newSubscription);
    }

    public void deleteSubscription(String id) throws EntityNotFoundException {
        SubscriptionEntity subscription = subscriptionRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        subscriptionRepository.delete(subscription);
    }

    public CompletableFuture<SubscriptionEntity> addCityToSubscription(String id, String city) {
        CompletableFuture<WeatherData> weatherDataFuture = weatherService.test(city);
        CompletableFuture<List<Alert>> alertsFuture = weatherService.getAlerts(city);
    
        return CompletableFuture.allOf(weatherDataFuture, alertsFuture)
                .thenApplyAsync(ignored -> {
                    WeatherData weatherData = weatherDataFuture.join();
                    List<Alert> alerts = alertsFuture.join();
    
                    WeatherDataEntity weatherDataEntity = new WeatherDataEntity(
                            weatherData.getCity(),
                            weatherData.getTemperature(),
                            weatherData.getCondition(),
                            weatherData.getWindSpeed(),
                            weatherData.getWindDirection(),
                            weatherData.getPrecipitations(),
                            weatherData.getHumidity()
                    );
    
                    SubscriptionEntity subscriptionEntity = subscriptionRepository.findByUserId(id)
                            .orElseThrow(() -> new EntityNotFoundException(id));
    
                    subscriptionEntity.getCities().add(weatherDataEntity);
    
                    List<String> alertStrings = alerts.stream()
                            .map(Alert::toString)
                            .collect(Collectors.toList());
    
                    subscriptionEntity.getAlerts().addAll(alertStrings);
    
                    return subscriptionRepository.save(subscriptionEntity);
                });
    }
    
    

    
    public CompletableFuture<SubscriptionEntity> deleteCityFromSubscription(String id, String cityName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findByUserId(id);
            SubscriptionEntity subscriptionEntity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));

            List<WeatherDataEntity> cities = subscriptionEntity.getCities()
                    .stream()
                    .filter(city -> !city.getCity().equalsIgnoreCase(cityName))
                    .collect(Collectors.toList());

            subscriptionEntity.setCities(cities);

            return subscriptionRepository.save(subscriptionEntity);
        });
    }

    public List<String> getAlertsForUser(String id) throws EntityNotFoundException {
        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findByUserId(id);
        SubscriptionEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        return entity.getAlerts() != null ? entity.getAlerts() : List.of();
    }

    public SubscriptionEntity clearAlertsForUser(String id) {

        SubscriptionEntity subscriptionEntity = subscriptionRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(id)); 

        subscriptionEntity.setAlerts(new ArrayList<>()); 

        return subscriptionRepository.save(subscriptionEntity);
    }

    public void deleteAllSubscriptions() {
        subscriptionRepository.deleteAll();
    }
}
