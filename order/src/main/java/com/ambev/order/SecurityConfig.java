package com.ambev.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ambev.order.config.JwtFilter;
import com.ambev.order.config.TokenProvider;
import com.ambev.order.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(TokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs RESTful
            .authorizeHttpRequests(authz -> authz
                // Permitir acesso público a estas rotas
                .requestMatchers(
                    "/api/auth/**",               // Endpoints de autenticação
                    "/swagger-ui/**",             // Swagger UI
                    "/v3/api-docs/**",            // Documentação OpenAPI
                    "/swagger-ui/**", // Rotas específicas do serviço (caso necessário)
                    "/actuator/**",
                    "/management/**"
                ).permitAll()
                // Proteger o restante das rotas
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class); // Adicionar filtro JWT
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(tokenProvider, userDetailsService);
    }
}
