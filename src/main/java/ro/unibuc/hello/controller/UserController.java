package ro.unibuc.hello.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Timed(value = "user.get_all.time", description = "Time taken to retrieve all users")
    @Counted(value = "user.get_all.count", description = "Number of times all users are retrieved")
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @Timed(value = "user.get_by_id.time", description = "Time taken to retrieve a user by ID")
    @Counted(value = "user.get_by_id.count", description = "Number of times a user is retrieved by ID")
    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable("id") String id) throws EntityNotFoundException {
        return userService.getUserById(id);
    }

    @Timed(value = "user.delete_by_id.time", description = "Time taken to delete a user by ID")
    @Counted(value = "user.delete_by_id.count", description = "Number of times a user is deleted by ID")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") String id) throws EntityNotFoundException {
        userService.deleteUserById(id);
    }

    @Timed(value = "user.delete_all.time", description = "Time taken to delete all users")
    @Counted(value = "user.delete_all.count", description = "Number of times all users are deleted")
    @DeleteMapping
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @Timed(value = "user.get_last_active.time", description = "Time taken to fetch last active timestamp for a user")
    @Counted(value = "user.get_last_active.count", description = "Number of times last active timestamp is fetched for a user")
    @GetMapping("/{id}/last-active")
    public LocalDateTime getLastActiveAt(@PathVariable("id") String id) throws EntityNotFoundException {
        return userService.getLastActiveById(id);
    }

    @Timed(value = "user.update_last_active.time", description = "Time taken to update last active timestamp for a user")
    @Counted(value = "user.update_last_active.count", description = "Number of times last active timestamp is updated for a user")
    @PutMapping("/{id}/last-active")
    public UserEntity updateLastActive(@PathVariable("id") String id) throws EntityNotFoundException {
        return userService.updateLastActive(id);
    }
}
