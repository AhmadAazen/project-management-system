package com.pmgmt.controller;

import com.pmgmt.dto.AuthRequestDto;
import com.pmgmt.dto.JwtResponseDto;
import com.pmgmt.dto.RegisterRequestDto;
import com.pmgmt.entity.User;
import com.pmgmt.repository.UserRepository;
import com.pmgmt.security.JwtUtil;
import com.pmgmt.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // Register endpoint using RegisterRequestDto
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Login endpoint using AuthRequestDto
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody AuthRequestDto loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDto(token, userDetails.getUsername()));
    }
}
