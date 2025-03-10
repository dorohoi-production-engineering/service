package main.java.ro.unibuc.hello.data;
import org.springframework.data.annotation.Id;

public class LocationEntity {

    @Id
    private String id;
    private String city;
    private double latitude;
    private double longitude;

    public LocationEntity() {}

    public LocationEntity(String city, double latitude, double longitude) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
