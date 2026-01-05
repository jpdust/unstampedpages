package com.unstampedpages.service;

import com.unstampedpages.dao.UserDAO;
import com.unstampedpages.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User createUser(String firstName, String lastName, int age, String email) {
        User user = new User(null, firstName, lastName, age, email);
        return userDAO.save(user);
    }

    public Optional<User> getUser(Long id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> updateUser(Long id, String firstName, String lastName, int age, String email) {
        return userDAO.findById(id).map(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setEmail(email);
            return userDAO.save(user);
        });
    }

    public boolean deleteUser(Long id) {
        if (userDAO.existsById(id)) {
            userDAO.deleteById(id);
            return true;
        }
        return false;
    }
}
