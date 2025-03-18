package ro.unibuc.hello.dto;

public class WeatherData {

    private String city;
    private double temperature;
    private String condition;
    private double windSpeed;
    private String windDirection;
    private double precipitations;
    private double humidity;

    public WeatherData() {
    }

    public WeatherData(String city, double temperature, String condition, double windSpeed, String windDirection, double precipitations, double humidity) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.precipitations = precipitations;
        this.humidity = humidity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public double getPrecipitations() {
        return precipitations;
    }

    public void setPrecipitations(double precipitations) {
        this.precipitations = precipitations;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

}
