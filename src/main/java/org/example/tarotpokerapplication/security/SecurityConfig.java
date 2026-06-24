package org.example.tarotpokerapplication.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/game/**").permitAll()
                .requestMatchers("/", "/index.html", "/style.css", "/game.js",
                                 "/Background.png", "/Table.png",
                                 "/register.html", "/login.html", "/dashboard.html").permitAll()
                .requestMatchers("/h2-console", "/h2-console/**").hasRole("ADMIN")
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                .requestMatchers("/crud.html", "/crud.css", "/crud.js").hasRole("ADMIN")
                .requestMatchers("/api/dashboard", "/api/stats").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
