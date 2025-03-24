package ro.unibuc.hello.dto;

import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class WeatherDataTest {
    
    private static String cityName = "Dorohoi";
    private static double temperature = 2.0;
    private static String condition = "cloudy";
    private static double windSpeed = 1.0;
    private static String windDirection = "W";
    private static double precipitations = 0.0;
    private static double humidity = 4.0;

    private static WeatherData weatherData = new WeatherData(cityName, temperature, condition, windSpeed, windDirection, precipitations, humidity);

    @Test
    public void test_content() {
        Assertions.assertEquals(cityName, weatherData.getCity());
        Assertions.assertEquals(temperature, weatherData.getTemperature());
        Assertions.assertEquals(condition, weatherData.getCondition());
        Assertions.assertEquals(windSpeed, weatherData.getWindSpeed());
        Assertions.assertEquals(windDirection, weatherData.getWindDirection());
        Assertions.assertEquals(precipitations, weatherData.getPrecipitations());
        Assertions.assertEquals(humidity, weatherData.getHumidity());
    }

    @Test
    public void test_setters() {
        weatherData.setCity("Bucuresti");
        weatherData.setTemperature(1.0);
        weatherData.setCondition("sunny");
        weatherData.setWindSpeed(3.4);
        weatherData.setWindDirection("E");
        weatherData.setPrecipitations(4.4);
        weatherData.setHumidity(2.0);

        Assertions.assertEquals("Bucuresti", weatherData.getCity());
        Assertions.assertEquals(1.0, weatherData.getTemperature());
        Assertions.assertEquals("sunny", weatherData.getCondition());
        Assertions.assertEquals(3.4, weatherData.getWindSpeed());
        Assertions.assertEquals("E", weatherData.getWindDirection());
        Assertions.assertEquals(4.4, weatherData.getPrecipitations());
        Assertions.assertEquals(2.0, weatherData.getHumidity());
    }
}
