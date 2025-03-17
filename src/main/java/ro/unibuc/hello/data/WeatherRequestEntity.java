package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

public class WeatherRequestEntity {

    @Id
    private String id;
    private String city;
    private LocalDateTime requestTime;

    public WeatherRequestEntity() {}

    public WeatherRequestEntity(String city, LocalDateTime requestTime) {
        this.city = city;
        this.requestTime = requestTime;
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

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
}
