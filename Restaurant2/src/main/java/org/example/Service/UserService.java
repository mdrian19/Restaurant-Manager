package org.example.Service;

import org.example.Entity.User;
import org.example.Repository.UserRepository;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void registerUser(String username, String password, User.Role role) {
        User user = new User(username, password, role);
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.getUsersByRole(role);
    }

    public void seedAdminUser() {
        Optional<User> admin = userRepository.findByUsername("admin");
        if (admin.isEmpty()) {
            registerUser("admin", "1q2w3e", User.Role.ADMIN);
        }
        else{
            System.out.println("Admin user already exists.");
        }
    }
}
