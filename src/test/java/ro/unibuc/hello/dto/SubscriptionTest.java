package ro.unibuc.hello.dto;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.WeatherDataEntity;

public class SubscriptionTest {
    
    private static String userId = UUID.randomUUID().toString();
    private static String cityName1 = "Bucuresti";
    private static String cityName2 = "Dorohoi";
    private static double temperature = 2.0;
    private static String condition = "cloudy";
    private static double windSpeed = 1.0;
    private static String windDirection = "W";
    private static double precipitations = 0.0;
    private static double humidity = 4.0;

    private static WeatherDataEntity bucuresti = new WeatherDataEntity(cityName1, temperature, condition, windSpeed, windDirection, precipitations, humidity);
    private static WeatherDataEntity dorohoi = new WeatherDataEntity(cityName2, temperature, condition, windSpeed, windDirection, precipitations, humidity);

    private static List<WeatherDataEntity> cityList = List.of(bucuresti, dorohoi);
    private static List<String> alerts = List.of("Alert1", "Alert2");

    private static SubscriptionEntity subscription = new SubscriptionEntity(userId, cityList, alerts);

    @Test
    public void test_content() {
        Assertions.assertEquals(userId, subscription.getId());
        Assertions.assertEquals(cityList, subscription.getCities());
        Assertions.assertEquals(alerts, subscription.getAlerts());
    }

    @Test
    public void test_setters() {
        List<String> alertList = List.of("Alert3", "Alert4");
        WeatherDataEntity cluj = new WeatherDataEntity("Cluj", temperature, condition, windSpeed, windDirection, precipitations, humidity);
        WeatherDataEntity iasi = new WeatherDataEntity("Iasi", temperature, condition, windSpeed, windDirection, precipitations, humidity);
        List<WeatherDataEntity> cities = List.of(cluj, iasi);
        String user = UUID.randomUUID().toString();

        subscription.setId(user);
        subscription.setCities(cities);
        subscription.setAlerts(alertList);

        Assertions.assertEquals(user, subscription.getId());
        Assertions.assertEquals(cities, subscription.getCities());
        Assertions.assertEquals(alertList, subscription.getAlerts());
    }
}
