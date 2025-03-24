package ro.unibuc.hello.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.hello.data.CookieEntity;
import ro.unibuc.hello.data.CookieRepository;
import ro.unibuc.hello.data.SubscriptionEntity;
import ro.unibuc.hello.data.SubscriptionRepository;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private CookieRepository cookieRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private UserService userService = new UserService();;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
    public void testCreateUser() {
        UserEntity user = new UserEntity();
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity createdUser = userService.createUser();

        assertNotNull(createdUser);
        verify(subscriptionRepository, times(1)).save(any(SubscriptionEntity.class));
    }

    @Test
    public void testGetAllUsers() {
        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetUserById_Existing() throws EntityNotFoundException {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        UserEntity result = userService.getUserById("1");

        assertNotNull(result);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById("no-user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("not-found"));
    }

    @Test
    public void testGetUserBySessionId_Existing() throws EntityNotFoundException {
        UserEntity user = new UserEntity();
        when(userRepository.findBySessionId("1")).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.getUserBySessionId("1");

        assertNotNull(result);
    }

    @Test
    public void testGetUserBySessionId_NotFound() {
        when(userRepository.findBySessionId("no-user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("no-user"));
    }

    @Test
    public void testGetLastActiveById_Existing() throws EntityNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setLastActiveAt(now);
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        assertEquals(now, userService.getLastActiveById("1"));
    }

    @Test
    public void testDeleteUserById_Existing() throws EntityNotFoundException {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        userService.deleteUserById("1");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserById_NotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById("1"));
    }

    @Test
    public void testDeleteAllUsers() {
        userService.deleteAllUsers();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    public void testUpdateLastActive() throws EntityNotFoundException {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity updatedUser = userService.updateLastActive("1");

        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getLastActiveAt());
    }

    @Test
    public void testCreateUserWithSession() {
        UserEntity user = new UserEntity();
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity created = userService.createUserWithSession();

        assertNotNull(created);
        assertNotNull(created.getSessionId());
        verify(cookieRepository, times(1)).save(any(CookieEntity.class));
    }

    @Test
    public void testGetUserBySessionIdFromCookie_Found() {
        String sessionId = "sessionId";
        CookieEntity cookie = new CookieEntity(sessionId, "1", LocalDateTime.now());
        UserEntity user = new UserEntity();
        when(cookieRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cookie));
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.getUserBySessionIdFromCookie(sessionId);

        assertTrue(result.isPresent());
    }

    @Test
    public void testGetUserBySessionIdFromCookie_NotFound() {
        when(cookieRepository.findBySessionId("sessionId")).thenReturn(Optional.empty());

        Optional<UserEntity> result = userService.getUserBySessionIdFromCookie("sessionId");

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        CookieEntity expired = new CookieEntity("sessionId1", "1", now.minusDays(1));
        CookieEntity valid = new CookieEntity("sessionId2", "1", now.plusDays(1));
        when(cookieRepository.findAll()).thenReturn(List.of(expired, valid));

        userService.deleteExpiredSessions();

        verify(cookieRepository, times(1)).deleteAll(List.of(expired));
    }

    @Test
    public void testDeleteSessionById_Found() {
        CookieEntity cookie = new CookieEntity("sessionId", "1", LocalDateTime.now());
        when(cookieRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cookie));

        userService.deleteSessionById("sessionId");

        verify(cookieRepository, times(1)).delete(cookie);
    }

    @Test
    public void testDeleteSessionById_NotFound() {
        when(cookieRepository.findBySessionId("sessionId")).thenReturn(Optional.empty());

        userService.deleteSessionById("sessionId");

        verify(cookieRepository, never()).delete(any());
    }
}
