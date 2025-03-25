package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); 
    }

    @Test
    public void testCreateSubscription() {
        String userId = "user123";
        SubscriptionEntity newSubscription = new SubscriptionEntity(userId, new ArrayList<>(), new ArrayList<>());
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(newSubscription);

        SubscriptionEntity result = subscriptionService.createSubscription(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertTrue(result.getCities().isEmpty());
        assertTrue(result.getAlerts().isEmpty());
    }

    @Test
    public void testGetAllCitiesForUser() {
        String userId = "user123";
        List<WeatherDataEntity> mockCities = List.of(new WeatherDataEntity("Test City", 20.0, "Clear", 5.0, "North", 0.0, 50.0));
        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, mockCities, new ArrayList<>());

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));

        List<WeatherDataEntity> result = subscriptionService.getAllCitiesForUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test City", result.get(0).getCity());
    }

    @Test
    public void testGetAllCitiesForUser_EntityNotFound() {
        String userId = "user123";
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.getAllCitiesForUser(userId);
        });

        assertEquals("Entity: user123 was not found", exception.getMessage());
    }

    @Test
    public void testDeleteSubscription() {
        String userId = "user123";
        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, new ArrayList<>(), new ArrayList<>());
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.deleteSubscription(userId);

        verify(subscriptionRepository, times(1)).delete(mockSubscription);
    }

    @Test
    public void testDeleteSubscription_EntityNotFound() {
        String userId = "user123";
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.deleteSubscription(userId);
        });

        assertEquals("Entity: user123 was not found", exception.getMessage());
    }

    @Test
    public void testAddCityToSubscription() {
        String userId = "user123";
        String city = "Test City";
        WeatherData mockWeatherData = new WeatherData(city, 25.0, "Clear", 10.0, "North", 0.0, 60.0);
        List<Alert> mockAlerts = List.of(new Alert("Alert1", "Warning", "Severe", "Immediate", "Area1", "Weather", "High", "Event", "Description", null, null, "", "Instructions"));

        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, new ArrayList<>(), new ArrayList<>());
        when(weatherService.test(city)).thenReturn(CompletableFuture.completedFuture(mockWeatherData));
        when(weatherService.getAlerts(city)).thenReturn(CompletableFuture.completedFuture(mockAlerts));
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(mockSubscription);

        CompletableFuture<SubscriptionEntity> resultFuture = subscriptionService.addCityToSubscription(userId, city);
        
        resultFuture.thenAccept(result -> {
            assertNotNull(result);
            assertEquals(1, result.getCities().size());
            assertEquals(city, result.getCities().get(0).getCity());
            assertEquals(1, result.getAlerts().size());
        });
    }

    @Test
    public void testAddCityToSubscription_EntityNotFound() {
        String userId = "user123";
        String city = "NonExistentCity";

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(weatherService.test(city)).thenReturn(CompletableFuture.completedFuture(null));
        when(weatherService.getAlerts(city)).thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<SubscriptionEntity> resultFuture = subscriptionService.addCityToSubscription(userId, city);

        resultFuture.exceptionally(ex -> {
            assertTrue(ex.getCause() instanceof EntityNotFoundException);
            return null;
        });
    }

    @Test
    public void testDeleteCityFromSubscription() {
        String userId = "user123";
        String cityName = "Test City";
        WeatherDataEntity cityEntity = new WeatherDataEntity(cityName, 20.0, "Clear", 5.0, "North", 0.0, 50.0);
        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, List.of(cityEntity), new ArrayList<>());

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(mockSubscription);

        CompletableFuture<SubscriptionEntity> resultFuture = subscriptionService.deleteCityFromSubscription(userId, cityName);

        resultFuture.thenAccept(result -> {
            assertNotNull(result);
            assertEquals(0, result.getCities().size());
        });
    }

    @Test
    public void testGetAlertsForUser() {
        String userId = "user123";
        List<String> mockAlerts = List.of("Alert1", "Alert2");
        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, new ArrayList<>(), mockAlerts);

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));

        List<String> result = subscriptionService.getAlertsForUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAlertsForUser_EntityNotFound() {
        String userId = "user123";
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.getAlertsForUser(userId);
        });

        assertEquals("Entity: user123 was not found", exception.getMessage());
    }

    @Test
    public void testClearAlertsForUser() {
        String userId = "user123";
        SubscriptionEntity mockSubscription = new SubscriptionEntity(userId, new ArrayList<>(), List.of("Alert1", "Alert2"));

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(mockSubscription));
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(mockSubscription);

        SubscriptionEntity result = subscriptionService.clearAlertsForUser(userId);

        assertNotNull(result);
        assertTrue(result.getAlerts().isEmpty());
    }

    @Test
    public void testClearAlertsForUser_EntityNotFound() {
        String userId = "user123";
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.clearAlertsForUser(userId);
        });

        assertEquals("Entity: user123 was not found", exception.getMessage());
    }
}
