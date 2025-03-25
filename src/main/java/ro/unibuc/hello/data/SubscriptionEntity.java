package ro.unibuc.hello.data;

import java.util.List;
import org.springframework.data.annotation.Id;
import ro.unibuc.hello.data.WeatherDataEntity;

public class SubscriptionEntity {
    @Id
    private String id;

    private String userId;
    private List<WeatherDataEntity> cities;
    private List<String> alerts;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(String userId, List<WeatherDataEntity> cities, List<String> alerts) {
        this.userId = userId;
        this.cities = cities;
        this.alerts = alerts;
    }

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    public List<WeatherDataEntity> getCities() {
        return cities;
    }

    public void setCities(List<WeatherDataEntity> cities) {
        this.cities = cities;
    }

    public List<String> getAlerts() {
        return alerts;
    }
    
    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

}
