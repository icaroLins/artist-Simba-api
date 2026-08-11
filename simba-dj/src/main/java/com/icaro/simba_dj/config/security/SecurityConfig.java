package com.icaro.simba_dj.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.config.jwt.JwtAuth;
import com.icaro.simba_dj.dto.response.erro.ErrorResponse;
import com.icaro.simba_dj.service.details.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuth jwtAuth;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${app.frontend-url}")
    private String frontendUrl;


    public SecurityConfig(JwtAuth jwtAuth, CustomUserDetailsService customUserDetailsService, ObjectMapper objectMapper) {
        this.jwtAuth = jwtAuth;
        this.customUserDetailsService = customUserDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(
                                "/simba/create",
                                "/simba/login",
                                "/mail/api/verify-email",
                                "/mail/api/resend-verification",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/v3/api-docs.yaml",
                                "/uploads/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    LocalDateTime.now(),
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    "Unauthorized",
                                    "Authentication is required.",
                                    request.getRequestURI()
                            );

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getWriter(), errorResponse);
                        }))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuth, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
