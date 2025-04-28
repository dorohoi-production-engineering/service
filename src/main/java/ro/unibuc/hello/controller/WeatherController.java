package ro.unibuc.hello.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.dto.Alert;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.WeatherService;
import ro.unibuc.hello.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Timed(value = "weather.test.time", description = "Time taken to fetch weather test data")
    @Counted(value = "weather.test.count", description = "Number of times weather test data is fetched")
    @GetMapping("/test/{city}")
    @ResponseBody
    public CompletableFuture<WeatherData> test(@PathVariable("city") String city) {
        return weatherService.test(city);
    }

    @Timed(value = "weather.get_alerts_from_api.time", description = "Time taken to fetch alerts from API")
    @Counted(value = "weather.get_alerts_from_api.count", description = "Number of times alerts from API are fetched")
    @GetMapping("/get-alerts-from-api/{city}")
    @ResponseBody
    public CompletableFuture<List<Alert>> getAlerts(@PathVariable("city") String city) {
        return weatherService.getAlerts(city);
    }

    @Timed(value = "weather.get_all.time", description = "Time taken to fetch all weather data")
    @Counted(value = "weather.get_all.count", description = "Number of times all weather data is fetched")
    @GetMapping("/get-all")
    @ResponseBody
    public List<WeatherData> getAllWeatherData() {
        return weatherService.getAllWeatherData();
    }

    @Timed(value = "weather.save.time", description = "Time taken to save weather data")
    @Counted(value = "weather.save.count", description = "Number of times weather data is saved")
    @PostMapping("/save/{city}")
    @ResponseBody
    public CompletableFuture<WeatherData> createWeatherData(@PathVariable String city) {
        return weatherService.saveWeatherData(city);
    }

    @Timed(value = "weather.update.time", description = "Time taken to update weather data")
    @Counted(value = "weather.update.count", description = "Number of times weather data is updated")
    @PutMapping("/update/{city}")
    @ResponseBody
    public CompletableFuture<WeatherData> updateWeatherData(@PathVariable String city) {
        return weatherService.updateWeatherData(city);
    }

    @Timed(value = "weather.delete.time", description = "Time taken to delete weather data")
    @Counted(value = "weather.delete.count", description = "Number of times weather data is deleted")
    @DeleteMapping("/delete/{city}")
    @ResponseBody
    public void deleteWeatherData(@PathVariable String city) throws EntityNotFoundException {
        weatherService.deleteWeatherData(city);
    }

    @Timed(value = "weather.get_subscription.time", description = "Time taken to get user's subscribed cities")
    @Counted(value = "weather.get_subscription.count", description = "Number of times user's subscribed cities are fetched")
    @GetMapping("/get-subscription/{userId}")
    @ResponseBody
    public List<WeatherDataEntity> getCitiesByUser(@PathVariable String userId) {
        return subscriptionService.getAllCitiesForUser(userId);
    }

    @Timed(value = "weather.post_subscription.time", description = "Time taken to create a subscription")
    @Counted(value = "weather.post_subscription.count", description = "Number of times a subscription is created")
    @PostMapping("/post-subscription/{id}")
    @ResponseBody
    public SubscriptionEntity createSubscription(@PathVariable String id) {
        return subscriptionService.createSubscription(id);
    }

    @Timed(value = "weather.delete_subscription.time", description = "Time taken to delete a subscription")
    @Counted(value = "weather.delete_subscription.count", description = "Number of times a subscription is deleted")
    @DeleteMapping("/delete-subscription/{id}")
    @ResponseBody
    public void deleteSubscription(@PathVariable String id) throws EntityNotFoundException {
        subscriptionService.deleteSubscription(id);
    }

    @Timed(value = "weather.add_city.time", description = "Time taken to add a city to subscription")
    @Counted(value = "weather.add_city.count", description = "Number of times a city is added to subscription")
    @PutMapping("/add-city/{id}-{city}")
    @ResponseBody
    public CompletableFuture<SubscriptionEntity> addCityToSubscription(@PathVariable String id, @PathVariable String city) {
        return subscriptionService.addCityToSubscription(id, city);
    }

    @Timed(value = "weather.remove_city.time", description = "Time taken to remove a city from subscription")
    @Counted(value = "weather.remove_city.count", description = "Number of times a city is removed from subscription")
    @PutMapping("/remove-city/{id}-{city}")
    @ResponseBody
    public void deleteCityFromSubscription(@PathVariable String id, @PathVariable String city) throws EntityNotFoundException {
        subscriptionService.deleteCityFromSubscription(id, city);
    }

    @Timed(value = "weather.get_alerts_by_user.time", description = "Time taken to get alerts for a user")
    @Counted(value = "weather.get_alerts_by_user.count", description = "Number of times alerts are fetched for a user")
    @GetMapping("/get-alerts/{userId}")
    @ResponseBody
    public List<String> getAlertsByUser(@PathVariable String userId) {
        return subscriptionService.getAlertsForUser(userId);
    }

    @Timed(value = "weather.clear_alerts_by_user.time", description = "Time taken to clear alerts for a user")
    @Counted(value = "weather.clear_alerts_by_user.count", description = "Number of times alerts are cleared for a user")
    @PutMapping("/clear-alerts/{userId}")
    @ResponseBody
    public SubscriptionEntity clearAlertsByUser(@PathVariable String userId) {
        return subscriptionService.clearAlertsForUser(userId);
    }
}
