package com.velto.service;

import com.velto.dto.RegisterRequest;
import com.velto.model.Role;
import com.velto.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(RegisterRequest request);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    List<User> findAllUsers();

    List<User> findUsersByRole(Role role);

    boolean existsByEmail(String email);
}
