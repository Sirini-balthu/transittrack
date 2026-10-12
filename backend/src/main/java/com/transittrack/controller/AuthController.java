
package com.transittrack.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.transittrack.entity.User;
import com.transittrack.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(
            @RequestBody Map<String, String> request) {

        User user = userService.registerStudent(
                request.get("name"),
                request.get("email"),
                request.get("password")
        );

        return Map.of(
                "message", "Student registered successfully",
                "userId", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String email = request.get("email");
        String password = request.get("password");

        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isEmpty()
                || password == null
                || !passwordEncoder.matches(
                        password,
                        optionalUser.get().getPasswordHash())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        User user = optionalUser.get();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return Map.of(
                "message", "Login successful",
                "userId", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(
            HttpServletRequest request) {

        var session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return Map.of("message", "Logged out successfully");
    }
        @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not logged in"));

        return Map.of(
                "userId", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }
}
