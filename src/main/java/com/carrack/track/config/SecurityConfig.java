package com.carrack.track.config;

import com.carrack.track.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * Notes:
 * - `PasswordEncoder` must match how passwords are hashed when created.
 * - `DaoAuthenticationProvider` delegates to `CustomUserDetailsService` to load users.
 * - `securityFilterChain` defines which URLs are public and which require authentication.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets up authentication provider to use the custom user details service
     * and the configured password encoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Configure HTTP security: public static resources and auth pages are permitted,
     * everything else requires authentication. Form login uses `/login` and the
     * `LoginSuccessHandler` to perform post-login actions (e.g. redirect + audit).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider provider, LoginSuccessHandler loginSuccessHandler) throws Exception {
        http.authenticationProvider(provider);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/register", "/login", "/error").permitAll()
                .anyRequest().authenticated()
        );
        http.formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .successHandler(loginSuccessHandler)
                .permitAll()
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );
        http.csrf(Customizer.withDefaults());
        return http.build();
    }
}
