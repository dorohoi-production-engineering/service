package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.CookieEntity;
import ro.unibuc.hello.data.CookieRepository;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieRepository cookieRepository;

    public UserEntity createUser() {
        UserEntity user = new UserEntity();
        return userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(String id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    public Optional<UserEntity> getUserBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId);
    }

    public LocalDateTime getLastActiveById(String id) throws EntityNotFoundException {
        UserEntity user =  userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return user.getLastActiveAt();
    }

    public void deleteUserById(String id) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        userRepository.delete(user);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public UserEntity updateLastActive(String id) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        user.setLastActiveAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public UserEntity createUserWithSession() {
        UserEntity user = new UserEntity();
        String sessionId = UUID.randomUUID().toString();
        user.setSessionId(sessionId);
        user = userRepository.save(user);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        CookieEntity cookie = new CookieEntity(sessionId, user.getId(), expiresAt);
        cookieRepository.save(cookie);

        return user;
    }

    public Optional<UserEntity> getUserBySessionIdFromCookie(String sessionId) {
        return cookieRepository.findBySessionId(sessionId)
                .flatMap(cookie -> userRepository.findById(cookie.getUserId()));
    }

    public void deleteExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<CookieEntity> expiredCookies = cookieRepository.findAll().stream()
                .filter(cookie -> cookie.getExpiresAt().isBefore(now))
                .toList();
        cookieRepository.deleteAll(expiredCookies);
    }

    public void deleteSessionById(String sessionId) {
        cookieRepository.findBySessionId(sessionId)
                .ifPresent(cookieRepository::delete);
    }
}
