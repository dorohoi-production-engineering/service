package ro.unibuc.hello.dto;

public class WeatherData {

    private String name;
    private double temperature;

    public WeatherData() {
    }

    public WeatherData(String name, double temperature) {
        this.name = name;
        this.temperature = temperature;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
