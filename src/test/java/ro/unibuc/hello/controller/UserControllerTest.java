package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void test_getAllUsers() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setId("1");
        UserEntity user2 = new UserEntity();
        user2.setId("2");
        List<UserEntity> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    void test_getUserById() throws Exception {
        UserEntity user = new UserEntity();
        user.setId("1");
        when(userService.getUserById("1")).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void test_deleteUserById() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById("1");
    }

    @Test
    void test_deleteAllUsers() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteAllUsers();
    }

    @Test
    void test_getLastActiveAt() throws Exception {
        LocalDateTime now = LocalDateTime.of(2025, 3, 25, 6, 24, 50);
        when(userService.getLastActiveById("1")).thenReturn(now);
    
        mockMvc.perform(get("/users/1/last-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(2025))
                .andExpect(jsonPath("$[1]").value(3))
                .andExpect(jsonPath("$[2]").value(25))
                .andExpect(jsonPath("$[3]").value(6))
                .andExpect(jsonPath("$[4]").value(24))
                .andExpect(jsonPath("$[5]").value(50));
    }    
    
    @Test
    void test_updateLastActive() throws Exception {
        UserEntity user = new UserEntity();
        user.setId("1");
        when(userService.updateLastActive("1")).thenReturn(user);

        mockMvc.perform(put("/users/1/last-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }
}
