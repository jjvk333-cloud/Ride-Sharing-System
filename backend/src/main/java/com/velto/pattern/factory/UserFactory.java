package com.velto.pattern.factory;

import com.velto.model.Role;
import com.velto.model.User;

/**
 * Factory Method pattern interface for User creation.
 */
public interface UserFactory {

    /**
     * Factory method creating the appropriate concrete User subclass based on Role.
     */
    User createUser(Role role, String name, String email, String password, String phone);
}
