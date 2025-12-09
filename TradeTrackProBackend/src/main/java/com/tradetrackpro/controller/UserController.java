package com.tradetrackpro.controller;

import com.tradetrackpro.dto.AuthResponse;
import com.tradetrackpro.dto.LoginRequest;
import com.tradetrackpro.dto.RegisterRequest;
import com.tradetrackpro.dto.SecurityQuestionRequest;
import com.tradetrackpro.dto.SecurityQuestionResponse;
import com.tradetrackpro.dto.VerifyAnswerRequest;
import com.tradetrackpro.dto.ResetPasswordRequest;
import com.tradetrackpro.model.User;
import com.tradetrackpro.security.JwtUtil;
import com.tradetrackpro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse resp = userService.register(request);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        System.out.println("Login attempt for: " + req.getUsername());
        User user = userService.validateUser(req.getUsername(), req.getPassword());
        if (user == null) {
            System.out.println("User not found or password mismatch");
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        System.out.println("Login successful for: " + user.getUsername());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                user.getUsername(),
                token,
                "Login successful"
        ));
    }

    @PostMapping("/get-security-question")
    public ResponseEntity<?> getSecurityQuestion(@RequestBody SecurityQuestionRequest request) {
        try {
            String question = userService.getSecurityQuestion(request.getUsername());
            return ResponseEntity.ok(new SecurityQuestionResponse(question));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/verify-security-answer")
    public ResponseEntity<?> verifySecurityAnswer(@RequestBody VerifyAnswerRequest request) {
        try {
            boolean isValid = userService.verifySecurityAnswer(request.getUsername(), request.getAnswer());
            return ResponseEntity.ok(Map.of("success", isValid));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getUsername(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successful"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", ex.getMessage()));
        }
    }
}

