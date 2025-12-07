package com.tradetrackpro.service;

import com.tradetrackpro.dto.AuthResponse;
import com.tradetrackpro.dto.LoginRequest;
import com.tradetrackpro.dto.RegisterRequest;
import com.tradetrackpro.model.User;
import com.tradetrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> { throw new IllegalArgumentException("Username already exists"); });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // TODO: hash in future
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return new AuthResponse(saved.getId(), saved.getUsername(), null, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        User user = validateUser(request.getUsername(), request.getPassword());
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return new AuthResponse(user.getId(), user.getUsername(), null, "Login successful");
    }

    public User validateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}
