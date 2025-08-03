package com.expensemanagement.filter;

import com.expensemanagement.service.RefreshTokenMap;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.expensemanagement.constants.AppConstants.*;

@RequiredArgsConstructor
public class JwtCreationFilter extends OncePerRequestFilter {

    private final RefreshTokenMap refreshTokenMap;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            Environment env = getEnvironment();
            String jwtSecretKey = env.getProperty(JWT_SECRET_KEY, JWT_DEFAULT_SECRET);
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
            String jwtToken = Jwts
                    .builder()
                    .issuer("ExpenseManagement-SriHarsha")
                    .subject("JWT Token for " + username)
                    .issuedAt(new Date())
                    .expiration(new Date(new Date().getTime() + 3600000)) // 1 hour expiration
                    .claim("username", username)
                    .claim("roles", roles)
                    .signWith(secretKey).compact();
            response.setHeader("jwtToken", jwtToken);
            String uuid = UUID.randomUUID().toString();
            refreshTokenMap.addRefreshToken(username, uuid);
            response.setHeader("REFRESH-TOKEN", uuid);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().contains(LOGIN_URL);
    }
}
