package ro.unibuc.hello.service;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.data.WeatherDataRepository;
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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private WeatherService weatherService;

    private final MeterRegistry meterRegistry;
    private final Counter createSubscriptionCounter;
    private final Counter saveCityCounter;
    private final Counter deleteCityCounter;
    private final Timer getAllCitiesTimer;
    private final Timer getAlertsTimer;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, WeatherService weatherService, MeterRegistry meterRegistry){
        this.subscriptionRepository = subscriptionRepository;
        this.weatherService = weatherService;
        this.meterRegistry = meterRegistry;

        this.createSubscriptionCounter = meterRegistry.counter("subsctiption_create_total");
        this.saveCityCounter = meterRegistry.counter("city_save_total");
        this.deleteCityCounter = meterRegistry.counter("city_delete_total");
        this.getAllCitiesTimer = meterRegistry.timer("city_get_all_duration");
        this.getAlertsTimer = meterRegistry.timer("city_get_alerts_duration");
        meterRegistry.gauge("sub_count", subscriptionRepository, SubscriptionRepository::count);
    }

    public List<WeatherDataEntity> getAllCitiesForUser(String id) throws EntityNotFoundException {
        Timer.Sample sample = Timer.start(meterRegistry);
        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findByUserId(id);
        SubscriptionEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        sample.stop(getAllCitiesTimer);
        return entity.getCities() != null ? entity.getCities() : List.of();
    }

    public SubscriptionEntity createSubscription(String id) {
        createSubscriptionCounter.increment();
        SubscriptionEntity newSubscription = new SubscriptionEntity(id, List.of(), List.of());
        return subscriptionRepository.save(newSubscription);
    }

    public void deleteSubscription(String id) throws EntityNotFoundException {
        deleteCityCounter.increment();
        SubscriptionEntity subscription = subscriptionRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        subscriptionRepository.delete(subscription);
    }

    public CompletableFuture<SubscriptionEntity> addCityToSubscription(String id, String city) {
        saveCityCounter.increment();
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
        deleteCityCounter.increment();
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
        Timer.Sample sample = Timer.start(meterRegistry);
        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findByUserId(id);
        SubscriptionEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        sample.stop(getAlertsTimer);
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
