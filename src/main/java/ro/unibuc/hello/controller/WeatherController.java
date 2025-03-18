package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.JsonNode;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.GreetingsService;
import ro.unibuc.hello.service.WeatherService;
import ro.unibuc.hello.service.SubscriptionService;

import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscription;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/test")
    @ResponseBody
    public CompletableFuture<WeatherData> test(String city) {
        return weatherService.test(city);
    }

    @GetMapping("/get-all")
    @ResponseBody
    public List<WeatherData> getAllWeatherData() {
        return weatherService.getAllWeatherData();
    }

    @PostMapping("/save/{city}")
    @ResponseBody
    public CompletableFuture<WeatherData> createWeatherData(@PathVariable String city) {
        return weatherService.saveWeatherData(city);
    }

    @PutMapping("/update/{city}")
    @ResponseBody
    public CompletableFuture<WeatherData> updateWeatherData(@PathVariable String city) {
        return weatherService.updateWeatherData(city);
    }


    @DeleteMapping("/delete/{city}")
    @ResponseBody
    public void deleteWeatherData(@PathVariable String city) throws EntityNotFoundException {
        weatherService.deleteWeatherData(city);
    }

    @GetMapping("/subscriptions/{userId}")
    public List<WeatherDataEntity> getCitiesByUser(@PathVariable String userId) {
        return subscriptionService.getAllCitiesForUser(userId);
    }
}

