package ro.unibuc.hello.dto;

public class WeatherData {

    private String city;
    private double temperature;

    public WeatherData() {
    }

    public WeatherData(String city, double temperature) {
        this.city = city;
        this.temperature = temperature;
    }


    public double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
