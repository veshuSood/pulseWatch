
package com.example.pulseWatch.service;

import com.example.pulseWatch.dto.LoginRequest;
import com.example.pulseWatch.dto.RegisterRequest;
import com.example.pulseWatch.entity.User;
import com.example.pulseWatch.repository.UserRepository;
import com.example.pulseWatch.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
            ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
        this.authenticationManager=authenticationManager;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(request.getName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider("LOCAL");
        user.setRole("USER");

        userRepository.save(user);
    }
    public String login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        return jwtService.generateToken(userDetails.getUser());
    }
    @Transactional
    public String loginWithGoogle(String email, String name) {
        String normalizedEmail = email.trim().toLowerCase();

        var existingUser = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (!"GOOGLE".equals(user.getAuthProvider())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This email is registered with password login."
                );
            }

            return jwtService.generateToken(user);
        }

        User newUser = new User();
        newUser.setEmail(normalizedEmail);
        newUser.setName(name);
        newUser.setAuthProvider("GOOGLE");
        newUser.setRole("USER");

        User savedUser = userRepository.save(newUser);

        return jwtService.generateToken(savedUser);
    }
}
