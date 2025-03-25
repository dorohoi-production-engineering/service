package ro.unibuc.hello.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.SubscriptionService;
import ro.unibuc.hello.service.WeatherService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 
    }

    @Test
    void testWeatherData() {
        WeatherData weatherData = new WeatherData();
        when(weatherService.test("London")).thenReturn(CompletableFuture.completedFuture(weatherData));
        CompletableFuture<WeatherData> result = weatherController.test("London");
        assertEquals(weatherData, result.join());
    }

    @Test
    void testGetAlerts() {
        List<Alert> alerts = Arrays.asList(new Alert(), new Alert());
        when(weatherService.getAlerts("London")).thenReturn(CompletableFuture.completedFuture(alerts));
        CompletableFuture<List<Alert>> result = weatherController.getAlerts("London");
        assertEquals(alerts, result.join());
    }

    @Test
    void testGetAllWeatherData() {
        List<WeatherData> dataList = Arrays.asList(new WeatherData(), new WeatherData());
        when(weatherService.getAllWeatherData()).thenReturn(dataList);
        assertEquals(dataList, weatherController.getAllWeatherData());
    }

    @Test
    void testCreateWeatherData() {
        WeatherData weatherData = new WeatherData();
        when(weatherService.saveWeatherData("Paris")).thenReturn(CompletableFuture.completedFuture(weatherData));
        CompletableFuture<WeatherData> result = weatherController.createWeatherData("Paris");
        assertEquals(weatherData, result.join());
    }

    @Test
    void testUpdateWeatherData() {
        WeatherData weatherData = new WeatherData();
        when(weatherService.updateWeatherData("Berlin")).thenReturn(CompletableFuture.completedFuture(weatherData));
        CompletableFuture<WeatherData> result = weatherController.updateWeatherData("Berlin");
        assertEquals(weatherData, result.join());
    }

    @Test
    void testDeleteWeatherData() throws EntityNotFoundException {
        doNothing().when(weatherService).deleteWeatherData("Tokyo");
        assertDoesNotThrow(() -> weatherController.deleteWeatherData("Tokyo"));
    }

    @Test
    void testGetCitiesByUser() {
        List<WeatherDataEntity> cities = Arrays.asList(new WeatherDataEntity(), new WeatherDataEntity());
        when(subscriptionService.getAllCitiesForUser("123")).thenReturn(cities);
        assertEquals(cities, weatherController.getCitiesByUser("123"));
    }

    @Test
    void testCreateSubscription() {
        SubscriptionEntity subscription = new SubscriptionEntity();
        when(subscriptionService.createSubscription("456")).thenReturn(subscription);
        assertEquals(subscription, weatherController.createSubscription("456"));
    }

    @Test
    void testDeleteSubscription() throws EntityNotFoundException {
        doNothing().when(subscriptionService).deleteSubscription("789");
        assertDoesNotThrow(() -> weatherController.deleteSubscription("789"));
    }

    @Test
    void testAddCityToSubscription() {
        SubscriptionEntity subscription = new SubscriptionEntity();
        when(subscriptionService.addCityToSubscription("123", "Rome")).thenReturn(CompletableFuture.completedFuture(subscription));
        CompletableFuture<SubscriptionEntity> result = weatherController.addCityToSubscription("123", "Rome");
        assertEquals(subscription, result.join());
    }

    @Test
    public void testDeleteCityFromSubscription() throws Exception {
        String id = "123";
        String city = "NewYork";
        when(subscriptionService.deleteCityFromSubscription(id, city)).thenReturn(null);
        weatherController.deleteCityFromSubscription(id, city);
        verify(subscriptionService).deleteCityFromSubscription(id, city);
    }

    @Test
    void testGetAlertsByUser() {
        List<String> alerts = Arrays.asList("Storm Warning", "Heavy Rain Alert");
        when(subscriptionService.getAlertsForUser("987")).thenReturn(alerts);
        assertEquals(alerts, weatherController.getAlertsByUser("987"));
    }

    @Test
    void testClearAlertsByUser() {
        SubscriptionEntity subscription = new SubscriptionEntity();
        when(subscriptionService.clearAlertsForUser("654")).thenReturn(subscription);
        assertEquals(subscription, weatherController.clearAlertsByUser("654"));
    }
}
