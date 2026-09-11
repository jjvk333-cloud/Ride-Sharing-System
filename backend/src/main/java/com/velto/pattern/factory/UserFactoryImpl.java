package com.velto.pattern.factory;

import com.velto.model.*;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the Factory Method pattern for User creation.
 * Encapsulates role-specific instantiation, default values, and role validation.
 */
@Component
public class UserFactoryImpl implements UserFactory {

    @Override
    public User createUser(Role role, String name, String email, String password, String phone) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        return switch (role) {
            case PASSENGER -> new Passenger(name, email, password, phone);
            case DRIVER -> new Driver(name, email, password, phone);
            case ADMIN -> new Admin(name, email, password, phone);
        };
    }
}
