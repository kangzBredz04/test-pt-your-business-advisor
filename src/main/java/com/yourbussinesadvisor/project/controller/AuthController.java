package com.yourbussinesadvisor.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.yourbussinesadvisor.project.model.User;
import com.yourbussinesadvisor.project.repository.UserRepository;
import com.yourbussinesadvisor.project.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final JwtService jwtService;

    @Autowired
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        try {
            // Enkripsi password sebelum menyimpan
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Simpan pengguna ke tabel user
            User savedUser = userRepository.save(user);

            // Kembalikan respons yang sesuai
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Registration success");
            responseBody.put("email", savedUser.getEmail());
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> loginRequest,
            HttpServletResponse response) throws JOSEException {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtService.create(user.getId().toString());

                Cookie cookie = new Cookie("token", token);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(60 * 60 * 24 * 7);
                cookie.setPath("/");
                response.addCookie(cookie);

                // Kembalikan respons yang sesuai
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", "Login success");
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("name", user.getName());
                userInfo.put("email", user.getEmail());
                userInfo.put("accessToken", token);
                responseBody.put("user", userInfo);
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("me")
    public ResponseEntity<?> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication or principal is null
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthenticated."));
        }

        // Safely cast the principal to User if it exists
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user); // Return the user if authenticated
    }

    @PostMapping("logout")
    public ResponseEntity<Map<String, String>> signOut(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        boolean tokenFound = false;

        // Cek apakah cookie ada token
        if (cookies != null) { // Tambahkan pengecekan null
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    tokenFound = true;
                    break;
                }
            }
        }

        if (!tokenFound) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Unauthenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        // Hapus token dari cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout success");
        return ResponseEntity.ok(responseBody);
    }
}
