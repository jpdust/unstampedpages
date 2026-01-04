package com.unstampedpages.service;

import com.unstampedpages.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public User createUser(String firstName, String lastName, int age, String email) {
        User user = new User(idCounter.getAndIncrement(), firstName, lastName, age, email);
        users.add(user);
        return user;
    }

    public Optional<User> getUser(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> updateUser(Long id, String firstName, String lastName, int age, String email) {
        return getUser(id).map(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setEmail(email);
            return user;
        });
    }

    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
}
