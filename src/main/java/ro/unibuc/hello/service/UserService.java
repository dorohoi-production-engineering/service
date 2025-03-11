package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}
