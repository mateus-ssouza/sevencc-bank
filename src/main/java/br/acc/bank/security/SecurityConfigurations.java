package br.acc.bank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/cliente/meu-perfil").hasRole("USUARIO")
                        .requestMatchers(HttpMethod.GET, "/cliente/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/cliente").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/cliente/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/cliente/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/agencia").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/agencia/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/agencia/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/conta/extrato").hasRole("USUARIO")
                        .requestMatchers(HttpMethod.GET, "/conta/minha-conta").hasRole("USUARIO")
                        .requestMatchers(HttpMethod.GET, "/conta/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/conta").hasRole("USUARIO")
                        .requestMatchers(HttpMethod.DELETE, "/conta/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/transacao/**").hasRole("USUARIO")
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
