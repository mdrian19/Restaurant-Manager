package org.example.Service;

import org.example.Entity.User;
import org.example.Repository.UserRepository;
import java.util.List;

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

    public User getUsersByRole(User.Role role) {
        return userRepository.getUsersByRole(role);
    }

    public void seedAdminUser() {
        List<User> users = userRepository.getAllUsers();
        boolean adminExists = users.stream().anyMatch(user -> user.getRole() == User.Role.ADMIN);
        if (!adminExists) {
            User adminUser = new User("admin", "1q2w3e", User.Role.ADMIN);
            registerUser("admin", "1q2w3e", User.Role.ADMIN);
            userRepository.save(adminUser);
        }
    }
}
