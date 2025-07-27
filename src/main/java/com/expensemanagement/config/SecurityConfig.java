package com.expensemanagement.config;

import com.expensemanagement.entities.Customer;
import com.expensemanagement.filter.JwtCreationFilter;
import com.expensemanagement.filter.JwtValidationFilter;
import com.expensemanagement.repositories.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.expensemanagement.constants.AppConstants.ALLOWED_ENDPOINTS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomerRepo customerRepo;

    @Bean
    SecurityFilterChain httpSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(corsConfig ->
                        corsConfig.configurationSource(request -> {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();
                            corsConfiguration.addAllowedOrigin("https://funintech.in");
                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.addAllowedMethod("*");
                            corsConfiguration.addAllowedHeader("*");
                            corsConfiguration.setExposedHeaders(List.of("Authorization", "Content-Type"));
                            corsConfiguration.setMaxAge(3600L);
                            return corsConfiguration;
                        })
                )
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorizer ->
                                authorizer
                                        .requestMatchers(ALLOWED_ENDPOINTS).permitAll()
                                        .anyRequest().authenticated()
                )
                .csrf(csrfConfigurer ->
                        csrfConfigurer
                                .ignoringRequestMatchers(ALLOWED_ENDPOINTS)
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .addFilterBefore(new JwtValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtCreationFilter(), BasicAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(loc -> loc
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "X-XSRF-TOKEN")
                        .clearAuthentication(true)
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService jpaUserDetailsService() {
        return username -> {
            Customer customer = customerRepo.findByUserId(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Customer with " + username + " is not found"));
            return User
                    .builder()
                    .username(username)
                    .password(customer.getPassword())
                    .authorities(customer.getRole()).build();
        };
    }

}
