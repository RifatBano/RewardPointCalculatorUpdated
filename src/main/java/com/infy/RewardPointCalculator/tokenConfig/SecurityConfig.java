package com.infy.RewardPointCalculator.tokenConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.infy.RewardPointCalculator.service.CustomerService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	private static final Logger log=LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtTokenFilter jwtAuthenticationFilter;
    private final CustomerService userDetailsService;

    // Constructor for dependency injection (Spring will inject the required dependencies here)
    @Autowired
    public SecurityConfig(JwtTokenFilter jwtAuthenticationFilter, CustomerService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // Bean to configure AuthenticationProvider (DaoAuthenticationProvider)
    @Bean
    
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Ensure UserDetailsService is injected
        provider.setPasswordEncoder(passwordEncoder()); // Ensure PasswordEncoder is set to BCryptPasswordEncoder
        return provider;
    }

    @PostConstruct
    public void init() {
        log.info("SecurityConfig initialized, JwtTokenFilter: " + jwtAuthenticationFilter);
    }
    // Use a more secure password encoder for production, e.g., BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Prefer BCrypt for password encryption in production
    }

    // Bean for AuthenticationManager to handle authentication logic
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.headers().frameOptions().disable();
        httpSecurity.cors().and().csrf().disable();

        // Configuring the HTTP request authorization rules
        httpSecurity
            .authorizeHttpRequests()
            .requestMatchers("/api/customers/register","/api/customers/login","/api/customers/transactions/**","/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow unauthenticated access to login API
            .anyRequest().authenticated() // All other requests require authentication
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> 
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage()) // Handle authentication failure
            )
            .and()
            .authenticationProvider(authenticationProvider()) // Register the authentication provider
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before authentication

        return httpSecurity.build(); // Build and return the final SecurityFilterChain
    }
}
