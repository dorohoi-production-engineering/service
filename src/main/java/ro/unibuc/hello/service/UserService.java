package ro.unibuc.hello.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.CookieEntity;
import ro.unibuc.hello.data.CookieRepository;
import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CookieRepository cookieRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MeterRegistry meterRegistry;

    private final Counter createdUsersCounter;
    private final Counter deletedUsersCounter;
    private final Counter findUserRequestsCounter;
    private final Timer getAllUsersTimer;
    private final Timer updateLastActiveTimer;

    private final Counter dbErrorCounter;
    private final Counter serviceErrorCounter;

    @Autowired
    public UserService(UserRepository userRepository,
                       CookieRepository cookieRepository,
                       SubscriptionRepository subscriptionRepository,
                       MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.cookieRepository = cookieRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.meterRegistry = meterRegistry;

        this.createdUsersCounter = meterRegistry.counter("user_created_total");
        this.deletedUsersCounter = meterRegistry.counter("user_deleted_total");
        this.findUserRequestsCounter = meterRegistry.counter("user_find_requests_total");
        this.getAllUsersTimer = meterRegistry.timer("user_get_all_duration");
        this.updateLastActiveTimer = meterRegistry.timer("user_update_last_active_duration");
        this.dbErrorCounter = meterRegistry.counter("user_service_db_errors_total");
        this.serviceErrorCounter = meterRegistry.counter("user_service_errors_total");

        meterRegistry.gauge("user_count", userRepository, UserRepository::count);
    }

    public UserEntity createUser() {
        try {
            UserEntity user = userRepository.save(new UserEntity());
            SubscriptionEntity newSubscription = new SubscriptionEntity(user.getId(), List.of(), List.of());
            subscriptionRepository.save(newSubscription);
            createdUsersCounter.increment();
            return user;
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public List<UserEntity> getAllUsers() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<UserEntity> users = userRepository.findAll();
            sample.stop(getAllUsersTimer);
            return users;
        } catch (Exception e) {
            sample.stop(getAllUsersTimer);
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public UserEntity getUserById(String id) throws EntityNotFoundException {
        findUserRequestsCounter.increment();
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> {
                        serviceErrorCounter.increment();
                        return new EntityNotFoundException(id);
                    });
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public Optional<UserEntity> getUserBySessionId(String sessionId) {
        try {
            return userRepository.findBySessionId(sessionId);
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public LocalDateTime getLastActiveById(String id) throws EntityNotFoundException {
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        serviceErrorCounter.increment();
                        return new EntityNotFoundException(id);
                    });
            return user.getLastActiveAt();
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public void deleteUserById(String id) throws EntityNotFoundException {
        deletedUsersCounter.increment();
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        serviceErrorCounter.increment();
                        return new EntityNotFoundException(id);
                    });
            userRepository.delete(user);
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public void deleteAllUsers() {
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public UserEntity updateLastActive(String id) throws EntityNotFoundException {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        serviceErrorCounter.increment();
                        return new EntityNotFoundException(id);
                    });
            user.setLastActiveAt(LocalDateTime.now());
            UserEntity updated = userRepository.save(user);
            sample.stop(updateLastActiveTimer);
            return updated;
        } catch (Exception e) {
            sample.stop(updateLastActiveTimer);
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public UserEntity createUserWithSession() {
        UserEntity user = new UserEntity();
        String sessionId = UUID.randomUUID().toString();
        user.setSessionId(sessionId);
        try {
            user = userRepository.save(user);
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
            CookieEntity cookie = new CookieEntity(sessionId, user.getId(), expiresAt);
            cookieRepository.save(cookie);
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }

        createdUsersCounter.increment();
        return user;
    }

    public Optional<UserEntity> getUserBySessionIdFromCookie(String sessionId) {
        try {
            return cookieRepository.findBySessionId(sessionId)
                    .flatMap(cookie -> userRepository.findById(cookie.getUserId()));
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            return Optional.empty();
        }
    }

    public void deleteExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        try {
            List<CookieEntity> expiredCookies = cookieRepository.findAll().stream()
                    .filter(cookie -> cookie.getExpiresAt().isBefore(now))
                    .toList();
            cookieRepository.deleteAll(expiredCookies);
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }

    public void deleteSessionById(String sessionId) {
        try {
            cookieRepository.findBySessionId(sessionId)
                    .ifPresent(cookieRepository::delete);
        } catch (Exception e) {
            if (e instanceof com.mongodb.MongoTimeoutException ||
                (e.getCause() instanceof com.mongodb.MongoTimeoutException)) {
                dbErrorCounter.increment();
            }
            throw e;
        }
    }
}
