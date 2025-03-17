package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.UserService;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public UserEntity createUser() {
        return userService.createUser();
    }

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable String id) throws EntityNotFoundException {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable String id) throws EntityNotFoundException {
        userService.deleteUserById(id);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @GetMapping("/{id}/last-active")
    public LocalDateTime getLastActiveAt(@PathVariable String id) throws EntityNotFoundException {
        return userService.getLastActiveById(id);
    }

    @PutMapping("/{id}/last-active")
    public UserEntity updateLastActive(@PathVariable String id) throws EntityNotFoundException {
        return userService.updateLastActive(id);
    }
}
