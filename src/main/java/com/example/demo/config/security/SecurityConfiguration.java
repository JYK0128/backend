package com.example.demo.config.security;

import com.example.demo.config.security.filter.JwtAuthenticationFilter;
import com.example.demo.config.security.filter.JwtAuthorizationFilter;
import com.example.demo.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    ApplicationUserService applicationUserService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), passwordEncoder));
//                .addFilter(new JwtAuthorizationFilter(authenticationManager()));
        http
                .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .antMatchers("/member").hasAnyRole("ADMIN", "USER")
                .antMatchers("/member/**").hasAnyAuthority("ALL:READ", "USER:READ")
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .authorities("ROLE_ADMIN", "ALL:READ", "ALL:WRITE")
                .build();
//        auth.inMemoryAuthentication().withUser(adminUser);
        auth.authenticationProvider(daoAuthenticationProvider);
    }
}