package com.soumya.chatserver.service;


import com.soumya.chatserver.entity.User;
import com.soumya.chatserver.repository.UserRepository;
import com.soumya.chatserver.util.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    public void saveUser(User user) {
        repository.save(user);
    }

    public void disconnect(User user) {
        var storedUser = repository.findById(user.getId()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(int id) {
        Optional<User> result = repository.findById(id);
        User user = null;

        if (result.isPresent()) {
            user = result.get();
        } else {
            throw new RuntimeException("Did not find id - " + id);
        }
        return user;
    }

    public User findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    public void deleteById(int id) {

        repository.deleteById(id);
    }


}
