package ro.unibuc.hello.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.data.WeatherDataRepository;
import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final WeatherDataRepository weatherDataRepository;
    private final HttpClient client;
    private final MeterRegistry meterRegistry;

    private final Counter apiCallCounter;
    private final Counter saveDataCounter;
    private final Counter deleteDataCounter;
    private final Timer getAllDataTimer;
    private final Timer updateDataTimer;

    @Value("${weather.api.key}")
    private String API_KEY;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String ALERTS_URL = "http://api.weatherapi.com/v1/alerts.json";

    @Autowired
    public WeatherService(WeatherDataRepository weatherDataRepository,
                          HttpClient client,
                          MeterRegistry meterRegistry) {
        this.weatherDataRepository = weatherDataRepository;
        this.client = client;
        this.meterRegistry = meterRegistry;

        this.apiCallCounter     = meterRegistry.counter("weather_api_call_total");
        this.saveDataCounter    = meterRegistry.counter("weather_data_save_total");
        this.deleteDataCounter  = meterRegistry.counter("weather_data_delete_total");
        this.getAllDataTimer    = meterRegistry.timer("weather_data_get_all_duration");
        this.updateDataTimer    = meterRegistry.timer("weather_data_update_duration");
        meterRegistry.gauge("weather_data_count", weatherDataRepository, WeatherDataRepository::count);
    }

    public CompletableFuture<WeatherData> test(String city) {
        apiCallCounter.increment();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?key=" + API_KEY + "&q=" + city))
                .GET()
                .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        JsonNode jsonResponse = objectMapper.readTree(response.body());

                        String cityName = jsonResponse.get("location").get("name").asText();
                        double temperature = jsonResponse.get("current").get("temp_c").asDouble();
                        String condition = jsonResponse.get("current").get("condition").get("text").asText();
                        double wind_speed = jsonResponse.get("current").get("wind_kph").asDouble();
                        String wind_direction = jsonResponse.get("current").get("wind_dir").asText();
                        double precipitations = jsonResponse.get("current").get("precip_mm").asDouble();
                        double humidity = jsonResponse.get("current").get("humidity").asDouble();

                        WeatherData weatherData = new WeatherData(cityName, temperature, condition, wind_speed,
                        wind_direction, precipitations, humidity);

                        return weatherData;
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON", e);
                    }
                });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<Alert>> getAlerts(String city) {
    apiCallCounter.increment();
    try {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ALERTS_URL + "?key=" + API_KEY + "&q=" + city))
            .GET()
            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                try {
                    JsonNode jsonResponse = objectMapper.readTree(response.body());
                    JsonNode alertsNode = jsonResponse.path("alerts").path("alert");

                    List<Alert> alerts = new ArrayList<>();
                    if (alertsNode.isArray()) {
                        for (JsonNode alertNode : alertsNode) {
                            Alert alert = new Alert(
                                alertNode.get("headline").asText(),
                                alertNode.get("msgtype").asText(),
                                alertNode.get("severity").asText(),
                                alertNode.get("urgency").asText(),
                                alertNode.get("areas").asText(),
                                alertNode.get("category").asText(),
                                alertNode.get("certainty").asText(),
                                alertNode.get("event").asText(),
                                alertNode.get("note").asText(),
                                LocalDateTime.parse(alertNode.get("effective").asText(), DateTimeFormatter.ISO_DATE_TIME),
                                LocalDateTime.parse(alertNode.get("expires").asText(), DateTimeFormatter.ISO_DATE_TIME),
                                alertNode.get("desc").asText(),
                                alertNode.get("instruction").asText()
                            );
                            alerts.add(alert);
                        }
                    }

                    return alerts;
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing JSON", e);
                }
            });
    } catch (Exception e) {
        return CompletableFuture.failedFuture(e);
    }
}

    public List<WeatherData> getAllWeatherData() {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<WeatherDataEntity> entities = weatherDataRepository.findAll();
        sample.stop(getAllDataTimer);
        return entities.stream()
                .map(entity -> new WeatherData(entity.getCity(), entity.getTemperature(), entity.getCondition(),
                entity.getWindSpeed(), entity.getWindDirection(), entity.getPrecipitations(), entity.getHumidity()))
                .collect(Collectors.toList());
    }

    public CompletableFuture<WeatherData> saveWeatherData(String city) {
        saveDataCounter.increment();
        return test(city).thenApply(weatherData -> {
            WeatherDataEntity entity = new WeatherDataEntity();
            entity.setCity(weatherData.getCity());
            entity.setTemperature(weatherData.getTemperature());
            entity.setCondition(weatherData.getCondition());
            entity.setWindSpeed(weatherData.getWindSpeed());
            entity.setWindDirection(weatherData.getWindDirection());
            entity.setPrecipitations(weatherData.getPrecipitations());
            entity.setHumidity(weatherData.getHumidity());
    
            weatherDataRepository.save(entity);
            return new WeatherData(entity.getCity(), entity.getTemperature(), entity.getCondition(),
            entity.getWindSpeed(), entity.getWindDirection(), entity.getPrecipitations(), entity.getHumidity());
        });
    }

    public CompletableFuture<WeatherData> updateWeatherData(String city) {
        Timer.Sample sample = Timer.start(meterRegistry);
        return test(city).thenApply(weatherData -> {
            WeatherDataEntity entity = weatherDataRepository.findByCity(city)
                    .orElseThrow(() -> new EntityNotFoundException("City not found: " + city));
    
            entity.setTemperature(weatherData.getTemperature());
            entity.setCondition(weatherData.getCondition());
            entity.setWindSpeed(weatherData.getWindSpeed());
            entity.setWindDirection(weatherData.getWindDirection());
            entity.setPrecipitations(weatherData.getPrecipitations());
            entity.setHumidity(weatherData.getHumidity());
    
            weatherDataRepository.save(entity);
            
            sample.stop(updateDataTimer);
            return new WeatherData(entity.getCity(), entity.getTemperature(), entity.getCondition(),
            entity.getWindSpeed(), entity.getWindDirection(), entity.getPrecipitations(), entity.getHumidity());
        });
    }

    public void deleteWeatherData(String city) throws EntityNotFoundException {
        deleteDataCounter.increment();
        WeatherDataEntity entity = weatherDataRepository.findByCity(city)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(city)));
        weatherDataRepository.delete(entity);
    }

    public void deleteAllWeather() {
        deleteDataCounter.increment();
        weatherDataRepository.deleteAll();
    }
}