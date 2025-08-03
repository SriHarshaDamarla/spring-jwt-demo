package com.expensemanagement.controller;

import com.expensemanagement.entities.Customer;
import com.expensemanagement.service.CustomerService;
import com.expensemanagement.service.RefreshTokenMap;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static com.expensemanagement.constants.AppConstants.*;

@RestController
@RequiredArgsConstructor
public class TokenRefreshController {

    private final RefreshTokenMap refreshTokenMap;
    private final CustomerService customerService;
    private final Environment env;

    @PostMapping(REFRESH_TOKEN_URL)
    public ResponseEntity<Map<String,String>> refreshToken(@RequestHeader("REFRESH-TOKEN") String refreshToken) {
        String username = refreshTokenMap.getUserFromToken(refreshToken);
        if (username == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        } else {
            Customer customer = customerService.loadCustomerByUsername(username);
            String role = customer.getRole();
            String jwtSecretKey = env.getProperty(JWT_SECRET_KEY, JWT_DEFAULT_SECRET);
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
            String newAccessToken = Jwts
                    .builder()
                    .issuer("ExpenseManagement-SriHarsha")
                    .subject("JWT Token for " + username)
                    .issuedAt(new Date())
                    .expiration(new Date(new Date().getTime() + 3600000)) // 1 hour expiration
                    .claim("username", username)
                    .claim("roles", role)
                    .signWith(secretKey).compact();
            return ResponseEntity.ok(Map.of("jwtAccessToken", newAccessToken));
        }
    }
}
