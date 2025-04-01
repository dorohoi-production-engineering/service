package ro.unibuc.hello.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.data.WeatherDataRepository;
import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.service.WeatherService;
import ro.unibuc.hello.service.SubscriptionService;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class WeatherControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();

    @BeforeAll
    public static void setUpContainer() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDownContainer() {
        mongoDBContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        final String MONGO_URL = "mongodb://localhost:" + mongoDBContainer.getMappedPort(27017);
        registry.add("mongodb.connection.url", () -> MONGO_URL);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @BeforeEach
    public void cleanUpAndAddTestData() {
        weatherService.deleteAllWeather();
        subscriptionService.deleteAllSubscriptions();

        WeatherDataEntity dataEntity1 = new WeatherDataEntity("Dorohoi", 10.0, "Cloudy", 5.0, "E", 0.0, 5.0);
        WeatherDataEntity dataEntity2 = new WeatherDataEntity("Bucharest", 15.0, "Sunny", 5.0, "S", 0.0, 5.0);

        SubscriptionEntity subscriptionEntity = new SubscriptionEntity("1001", List.of(dataEntity1, dataEntity2), List.of("alert"));

        weatherDataRepository.save(dataEntity1);
        weatherDataRepository.save(dataEntity2);

        subscriptionRepository.save(subscriptionEntity);
    }

    @Test
    public void testGetWeatherData() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/weather/test/Dorohoi"))
            .andExpect(request().asyncStarted()) 
            .andReturn();


        mvcResult = mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.city").value("Dorohoi"))
            .andReturn();
       
    }

    @Test
    public void testGetAlerts() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/weather/get-alerts-from-api/Dorohoi"))
        .andExpect(request().asyncStarted()) 
        .andReturn();


    mvcResult = mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andReturn();
    }

    @Test
    public void testGetAllWeatherData() throws Exception {
        mockMvc.perform(get("/weather/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].city").exists())
                .andExpect(jsonPath("$[0].temperature").isNumber());
    }

    @Test
    public void testCreateWeatherData() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/weather/save/Paris"))
                .andExpect(request().asyncStarted()) 
                .andReturn();

        mvcResult = mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andReturn();
    }

    @Test
    public void testDeleteWeatherData() throws Exception {
        mockMvc.perform(get("/weather/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(delete("/weather/delete/Dorohoi"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/weather/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetSubscription() throws Exception {
        mockMvc.perform(get("/weather/get-subscription/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].city").value("Dorohoi"))
                .andExpect(jsonPath("$[0].temperature").value(10.0))
                .andExpect(jsonPath("$[0].condition").value("Cloudy"))
                .andExpect(jsonPath("$[0].windSpeed").value(5.0))
                .andExpect(jsonPath("$[0].windDirection").value("E"))
                .andExpect(jsonPath("$[0].precipitations").value(0.0))
                .andExpect(jsonPath("$[0].humidity").value(5.0));
    }

    @Test
    public void testCreateSubscription() throws Exception {
        mockMvc.perform(post("/weather/post-subscription/1002"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1002"));
    }

    @Test
    public void testDeleteSubscription() throws Exception {
        mockMvc.perform(delete("/weather/delete-subscription/1001"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddCityToSubscription() throws Exception {
        mockMvc.perform(get("/weather/get-subscription/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
        MvcResult mvcResult = mockMvc.perform(put("/weather/add-city/1001-Paris"))
            .andExpect(request().asyncStarted()) 
            .andReturn();
        mvcResult = mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andReturn();
        mockMvc.perform(get("/weather/get-subscription/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void testRemoveCityFromSubscription() throws Exception {
        mockMvc.perform(get("/weather/get-subscription/1001"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(put("/weather/remove-city/1001-Dorohoi"))
            .andExpect(status().isOk());
        Thread.sleep(1000);
        mockMvc.perform(get("/weather/get-subscription/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetAlertsByUser() throws Exception {
        mockMvc.perform(get("/weather/get-alerts/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testClearAlertsByUser() throws Exception {
        mockMvc.perform(put("/weather/clear-alerts/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/weather/get-alerts/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

}
