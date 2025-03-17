package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.JsonNode;

import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.GreetingsService;
import ro.unibuc.hello.service.WeatherService;

import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/test")
    @ResponseBody
    public CompletableFuture<WeatherData> test() {
        return weatherService.test();
    }

    @GetMapping("/weather")
    @ResponseBody
    public List<WeatherData> getAllWeatherData() {
        return weatherService.getAllWeatherData();
    }

    @PostMapping("/weather")
    @ResponseBody
    public WeatherData createGreeting(@RequestBody WeatherData weatherData) {
        return weatherService.saveWeatherData(weatherData);
    }


}

