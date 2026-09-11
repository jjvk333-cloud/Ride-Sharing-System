package com.velto.service;

import com.velto.dto.RegisterRequest;
import com.velto.model.Driver;
import com.velto.model.Role;
import com.velto.model.User;
import com.velto.pattern.factory.UserFactory;
import com.velto.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation utilizing UserFactory (Factory Method Pattern) to create users.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserFactory userFactory, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        // 1. Hash the password before saving
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Use Factory Method to instantiate the appropriate concrete User subclass
        User user = userFactory.createUser(
                request.getRole(),
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getPhone()
        );

        // 3. Set role-specific driver details if applicable
        if (user instanceof Driver driver) {
            if (request.getVehicleNumber() != null) driver.setVehicleNumber(request.getVehicleNumber());
            if (request.getVehicleType() != null) driver.setVehicleType(request.getVehicleType());
            if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        }

        // 4. Save into MongoDB
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
