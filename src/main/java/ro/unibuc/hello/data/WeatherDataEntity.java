package ro.unibuc.hello.data;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

public class WeatherDataEntity {

    @Id
    private String id;
    private String city;
    private double temperature;

    public WeatherDataEntity() {}

    public WeatherDataEntity(String city, double temperature) {
        this.city = city;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}

