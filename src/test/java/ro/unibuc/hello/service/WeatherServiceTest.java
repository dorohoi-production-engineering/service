package ro.unibuc.hello.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.data.WeatherDataRepository;
import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private WeatherService weatherService; 
    private SimpleMeterRegistry meterRegistry;

    private static final String MOCK_WEATHER_RESPONSE = "{\"location\": {\"name\": \"City\"}, \"current\": {\"temp_c\": 25.5, \"condition\": {\"text\": \"Clear\"}, \"wind_kph\": 10.0, \"wind_dir\": \"North\", \"precip_mm\": 0.0, \"humidity\": 60.0 } }";

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        meterRegistry = new SimpleMeterRegistry();

        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(httpResponse));
        when(httpResponse.body()).thenReturn(MOCK_WEATHER_RESPONSE);

        weatherService = new WeatherService(weatherDataRepository, httpClient, meterRegistry);
    }

    @Test
    public void testSaveWeatherData() {
        String city = "City";
        when(weatherDataRepository.save(any(WeatherDataEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        CompletableFuture<WeatherData> result = weatherService.saveWeatherData(city);
        WeatherData weatherData = result.join();

        assertNotNull(weatherData);
        assertEquals(city, weatherData.getCity());
    }

    @Test
    public void testUpdateWeatherData() {
        String city = "City";
        WeatherDataEntity mockEntity = new WeatherDataEntity();
        mockEntity.setCity(city);
        
        when(weatherDataRepository.findByCity(city)).thenReturn(Optional.of(mockEntity));
        when(weatherDataRepository.save(any(WeatherDataEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        CompletableFuture<WeatherData> result = weatherService.updateWeatherData(city);
        WeatherData weatherData = result.join();

        assertNotNull(weatherData);
        assertEquals(city, weatherData.getCity());
    }

    @Test
    public void testGetAllWeatherData() {
        WeatherDataEntity mockEntity = new WeatherDataEntity();
        mockEntity.setCity("City");
        when(weatherDataRepository.findAll()).thenReturn(List.of(mockEntity));

        List<WeatherData> result = weatherService.getAllWeatherData();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("City", result.get(0).getCity());
    }

    @Test
    public void testDeleteWeatherData() {
        String city = "City";
        WeatherDataEntity mockEntity = new WeatherDataEntity();
        mockEntity.setCity(city);

        when(weatherDataRepository.findByCity(city)).thenReturn(Optional.of(mockEntity));
        
        weatherService.deleteWeatherData(city);
        
        verify(weatherDataRepository, times(1)).delete(mockEntity);
    }

    @Test
    public void testDeleteWeatherData_EntityNotFound() {
        String city = "NonExistentCity";
        when(weatherDataRepository.findByCity(city)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            weatherService.deleteWeatherData(city);
        });
    }

    @Test
    public void testGetAlerts() {
        String city = "City";
        List<Alert> mockAlerts = List.of(new Alert("Alert1", "Warning", "Severe", "Immediate", "Area1", "Weather", "High", "Event", "Description", null, null, "", "Instructions"));

        CompletableFuture<List<Alert>> result = CompletableFuture.completedFuture(mockAlerts);
        List<Alert> alerts = result.join();

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals("Alert1", alerts.get(0).getHeadline());
    }

    @Test
    public void testTestFunction() {
        String city = "City";
        CompletableFuture<WeatherData> result = weatherService.test(city);
        WeatherData weatherData = result.join();

        assertNotNull(weatherData);
        assertEquals(city, weatherData.getCity());
        assertEquals(25.5, weatherData.getTemperature());
    }

    @Test
    public void testTestFunction_JsonParsingError() throws Exception {
        when(httpResponse.body()).thenReturn("INVALID JSON");
        CompletableFuture<WeatherData> result = weatherService.test("Test City");
        assertThrows(RuntimeException.class, result::join);
    }
}