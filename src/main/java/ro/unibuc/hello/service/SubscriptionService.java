package main.java.ro.unibuc.hello.service;

import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.WeatherDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public List<WeatherDataEntity> getAllCitiesForUser(String id) {
        Optional<SubscriptionEntity> subscription = subscriptionRepository.findById(id);
        
        if (subscription.isPresent()) {
            return subscription.get().getCities();
        } else {
            return List.of();
        }
    }
    
}
