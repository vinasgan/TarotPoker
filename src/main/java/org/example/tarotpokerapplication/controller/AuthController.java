package org.example.tarotpokerapplication.controller;

import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.db.User;
import org.example.tarotpokerapplication.db.UserRepository;
import org.example.tarotpokerapplication.security.JwtService;
import org.example.tarotpokerapplication.security.dto.AuthResponse;
import org.example.tarotpokerapplication.security.dto.LoginRequest;
import org.example.tarotpokerapplication.security.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @GetMapping("/register")
    public ResponseEntity<Void> registerForm() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/register.html")
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/login")
    public ResponseEntity<Void> loginForm() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/login.html")
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
