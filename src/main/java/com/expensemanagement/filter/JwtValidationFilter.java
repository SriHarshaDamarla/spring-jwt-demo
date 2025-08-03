package com.expensemanagement.filter;

import com.expensemanagement.service.RefreshTokenMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.expensemanagement.constants.AppConstants.*;

@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final RefreshTokenMap refreshTokenMap;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7);
                Environment env = getEnvironment();
                String jwtSecretKey = env.getProperty(JWT_SECRET_KEY, JWT_DEFAULT_SECRET);
                SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwtToken).getPayload();
                String username = claims.get("username", String.class);
                String roles = claims.get("roles", String.class);
                if(username != null && roles != null) {
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(username,null,
                                    AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String uuid = UUID.randomUUID().toString();
                    refreshTokenMap.addRefreshToken(username, uuid);
                    response.setHeader("REFRESH-TOKEN", uuid);
                } else {
                    throw new BadCredentialsException("JWT token is missing username or roles");
                }
            }
        } catch (SignatureException exception) {
            throw new BadCredentialsException("JWT token signature is not matching/token tampered with");
        } catch (ExpiredJwtException exception) {
            throw new BadCredentialsException("JWT token is expired");
        } catch (Exception exception) {
            throw new BadCredentialsException("JWT token is invalid");
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().contains(LOGIN_URL);
    }
}
