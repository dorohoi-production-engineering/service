package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class WeatherService {

    @Autowired
    private InformationRepository informationRepository;
    private HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper(); 
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";

    @Value("${weather.api.key}")
    private String API_KEY;
    
    public CompletableFuture<WeatherData> test() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?key=" + API_KEY + "&q=Paris"))
                .GET()
                .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        JsonNode jsonResponse = objectMapper.readTree(response.body());

                        String cityName = jsonResponse.get("location").get("name").asText();
                        double temperature = jsonResponse.get("current").get("temp_c").asDouble();

                        WeatherData weatherData = new WeatherData(cityName, temperature);

                        return weatherData;
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON", e);
                    }
                });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

   }
