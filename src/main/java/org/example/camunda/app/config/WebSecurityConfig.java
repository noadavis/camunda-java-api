package org.example.camunda.app.config;

import org.example.camunda.app.user.CustomAuthenticationProvider;
import org.example.camunda.app.user.CustomPasswordEncoder;
import org.example.camunda.app.user.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    AuthenticationManager authenticationManager;
    private final CustomUserDetailService userService;
    private final CustomAuthenticationProvider authProvider;
    private final CustomPasswordEncoder passEncoder;

    public WebSecurityConfig(CustomUserDetailService userService, CustomAuthenticationProvider authProvider, CustomPasswordEncoder passEncoder) {
        this.userService = userService;
        this.authProvider = authProvider;
        this.passEncoder = passEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .authenticationProvider(authProvider)
                .userDetailsService(userService)
                .passwordEncoder(passEncoder);
        authenticationManager = authenticationManagerBuilder.build();
        httpSecurity
                .authenticationManager(authenticationManager).authorizeRequests()
                .antMatchers("/error", "/engine-rest/**", "/auth/*", "/static/**")
                .permitAll().anyRequest().authenticated()
                .and().cors()
                .and().csrf().ignoringAntMatchers("/engine-rest/**").csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                .and().formLogin().loginPage("/auth/login").defaultSuccessUrl("/task")
                .and().logout().logoutSuccessUrl("/auth/login");
        return httpSecurity.build();
    }
}
