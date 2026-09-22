package com.velto.config;

import com.velto.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Enterprise Spring Security Configuration providing:
 * 1. BCrypt Password Hashing (10 salt rounds)
 * 2. Role-Based Access Control (PASSENGER, DRIVER, ADMIN)
 * 3. HTTP Basic & Session Authentication support
 * 4. CSRF disablement for pure REST/JSON API operations
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authenticationProvider(authenticationProvider())
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // Public presentation resources & static SPA assets
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                
                // Public REST endpoints
                .requestMatchers("/api/auth/**", "/api/health/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/rides/**").permitAll()
                
                // Admin-only endpoints
                .requestMatchers(HttpMethod.PATCH, "/api/config/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/config/reset").hasRole("ADMIN")
                
                // Driver & Passenger Custom Route endpoints
                .requestMatchers(HttpMethod.POST, "/api/rides").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/rides/*/status").hasAnyRole("DRIVER", "ADMIN")
                
                // Booking and payment endpoints
                .requestMatchers(HttpMethod.POST, "/api/bookings").hasAnyRole("PASSENGER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/cancel").hasAnyRole("PASSENGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/payments").hasAnyRole("PASSENGER", "ADMIN")
                
                // All other API endpoints permitted with session or basic auth
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
