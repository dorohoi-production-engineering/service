package main.java.ro.unibuc.hello.data;

import java.util.List;
import org.springframework.data.annotation.Id;
import ro.unibuc.hello.data.WeatherDataEntity;

public class SubscriptionEntity {
    @Id
    private String id;
    private List<WeatherDataEntity> cities;

    public SubscriptionEntity() {}

    public SubscriptionEntity(String id, List<WeatherDataEntity> cities) {
        this.id = id;
        this.cities = cities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<WeatherDataEntity> getCities() {
        return cities;
    }

    public void setCities(List<WeatherDataEntity> cities) {
        this.cities = cities;
    }
}
