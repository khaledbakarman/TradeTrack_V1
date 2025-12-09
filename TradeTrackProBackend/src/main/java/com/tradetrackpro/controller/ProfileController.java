package com.tradetrackpro.controller;

import com.tradetrackpro.dto.*;
import com.tradetrackpro.security.JwtUtil;
import com.tradetrackpro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUserId(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.getUserProfile(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update-username")
    public ResponseEntity<?> updateUsername(HttpServletRequest request, @RequestBody UpdateUsernameRequest req) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.updateUsername(userId, req.getNewUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update-display-name")
    public ResponseEntity<?> updateDisplayName(HttpServletRequest request, @RequestBody UpdateDisplayNameRequest req) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.updateDisplayName(userId, req.getDisplayName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-profile-picture")
    public ResponseEntity<?> updateProfilePicture(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.updateProfilePicture(userId, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove-profile-picture")
    public ResponseEntity<?> removeProfilePicture(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.removeProfilePicture(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest req) {
        try {
            Long userId = extractUserId(request);
            userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PutMapping("/change-security-question")
    public ResponseEntity<?> changeSecurityQuestion(HttpServletRequest request, @RequestBody ChangeSecurityQuestionRequest req) {
        try {
            Long userId = extractUserId(request);
            return ResponseEntity.ok(userService.changeSecurityQuestion(userId, req.getSecurityQuestion(), req.getSecurityAnswer()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
