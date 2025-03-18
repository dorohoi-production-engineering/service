package ro.unibuc.hello;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import ro.unibuc.hello.data.CookieRepository;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.service.UserService;

import ro.unibuc.hello.data.WeatherDataRepository;
import ro.unibuc.hello.dto.WeatherData;
import ro.unibuc.hello.data.WeatherDataEntity;
import ro.unibuc.hello.service.WeatherService;
import java.util.concurrent.CompletableFuture;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.SubscriptionEntity;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {InformationRepository.class, UserRepository.class})
public class HelloApplication {

	@Autowired
	private InformationRepository informationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CookieRepository cookieRepository;

	@Autowired
	private UserService userService;
  
	@Autowired
	private WeatherDataRepository weatherDataRepository;

	@Autowired
	private WeatherService weatherService;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
		informationRepository.deleteAll();
		informationRepository.save(new InformationEntity("Overview",
				"This is an example of using a data storage engine running separately from our applications server"));

		userRepository.deleteAll();
		userRepository.save(new UserEntity());
		userService.createUserWithSession();

		weatherDataRepository.deleteAll();
		weatherService.test("Dorohoi").thenAccept(newEntity -> {
			WeatherDataEntity anotherEntity = new WeatherDataEntity(
				newEntity.getCity(), 
				newEntity.getTemperature(), 
				newEntity.getCondition(),
				newEntity.getWindSpeed(), 
				newEntity.getWindDirection(), 
				newEntity.getPrecipitations(), 
				newEntity.getHumidity()
			);
		
			weatherDataRepository.save(anotherEntity);
		});
		subscriptionRepository.deleteAll();
		subscriptionRepository.save(new SubscriptionEntity());
		}

}
