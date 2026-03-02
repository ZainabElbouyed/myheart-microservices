// services/auth-service/src/main/java/com/myheart/auth/controller/AuthController.java
package com.myheart.auth.controller;

import com.myheart.auth.dto.LoginRequest;
import com.myheart.auth.dto.AuthResponse;
import com.myheart.auth.dto.RegisterRequest;
import com.myheart.auth.entity.User;
import com.myheart.auth.repository.UserRepository;
import com.myheart.auth.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build());
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        
        // 🔴 UTILISER LE RÔLE ENVOYÉ PAR LE FRONTEND
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                user.setRole(User.Role.valueOf(request.getRole()));
            } catch (IllegalArgumentException e) {
                // Si le rôle n'est pas valide, mettre PATIENT par défaut
                user.setRole(User.Role.PATIENT);
            }
        } else {
            user.setRole(User.Role.PATIENT);
        }
        
        userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String email = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        User user = userRepository.findByEmail(email).orElseThrow();
        
        return ResponseEntity.ok(AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build());
    }
}