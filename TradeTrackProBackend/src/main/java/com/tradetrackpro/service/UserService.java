package com.tradetrackpro.service;

import com.tradetrackpro.dto.AuthResponse;
import com.tradetrackpro.dto.LoginRequest;
import com.tradetrackpro.dto.RegisterRequest;
import com.tradetrackpro.dto.UserProfileResponse;
import com.tradetrackpro.model.User;
import com.tradetrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/avatars/";

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> { throw new IllegalArgumentException("Username already exists"); });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswerHash(hashAnswer(request.getSecurityAnswer()));
        user.setDisplayName(request.getUsername()); // Default display name = username
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

    public String getSecurityQuestion(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getSecurityQuestion();
    }

    public boolean verifySecurityAnswer(String username, String answer) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String hashedAnswer = hashAnswer(answer);
        return hashedAnswer.equals(user.getSecurityAnswerHash());
    }

    public void resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    // Profile Settings Methods
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .securityQuestion(user.getSecurityQuestion())
                .build();
    }

    public UserProfileResponse updateUsername(Long userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if username is taken
        userRepository.findByUsername(newUsername)
                .ifPresent(u -> { 
                    if (!u.getId().equals(userId)) {
                        throw new IllegalArgumentException("Username already exists"); 
                    }
                });
        
        user.setUsername(newUsername);
        userRepository.save(user);
        return getUserProfile(userId);
    }

    public UserProfileResponse updateDisplayName(Long userId, String displayName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setDisplayName(displayName);
        userRepository.save(user);
        return getUserProfile(userId);
    }

    public UserProfileResponse updateProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Update user profile picture URL
        String pictureUrl = "/uploads/avatars/" + newFilename;
        user.setProfilePictureUrl(pictureUrl);
        userRepository.save(user);
        
        return getUserProfile(userId);
    }

    public UserProfileResponse removeProfilePicture(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfilePictureUrl(null);
        userRepository.save(user);
        return getUserProfile(userId);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public UserProfileResponse changeSecurityQuestion(Long userId, String question, String answer) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSecurityQuestion(question);
        user.setSecurityAnswerHash(hashAnswer(answer));
        userRepository.save(user);
        return getUserProfile(userId);
    }

    private String hashAnswer(String answer) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(answer.toLowerCase().trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash security answer", e);
        }
    }
}


