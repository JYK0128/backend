package com.example.demo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private ClientRegistrationRepository clientRegistrationRepository;
    private OAuth2AuthorizedClientService authorizedClientService;
    private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

    @Autowired
    SecurityConfig(ClientRegistrationRepository clientRegistrationRepository,
                   OAuth2AuthorizedClientService authorizedClientService,
                   AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver){
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientService = authorizedClientService;
        this.authenticationManagerResolver = authenticationManagerResolver;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        protectionConfig(http);
        customFilterConfig(http);
        authorizationConfig(http);
        oAuthConfig(http);
    }

    private void protectionConfig(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private void customFilterConfig(HttpSecurity http) throws Exception {
//        http.addFilter(filter);
    }

    private void authorizationConfig(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/.~~spring-boot!~/restart").permitAll()
                .antMatchers("/", "/favicon.ico", "/oauth2/**", "/docs/**").permitAll()
                .antMatchers("/profile/**", "/member/**", "/post/**", "/message/**", "/upload/**").permitAll()
                .anyRequest().authenticated();
    }

    private void oAuthConfig(HttpSecurity http) throws Exception {
        //client
        http.oauth2Client(oauth2 -> oauth2
                .clientRegistrationRepository(clientRegistrationRepository)
                .authorizedClientService(authorizedClientService) // or authorizedClientRepository
                .authorizationCodeGrant(codeGrant -> codeGrant
                        .authorizationRequestResolver(new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")) // or authorizationRequestRepository
                        .accessTokenResponseClient(new DefaultAuthorizationCodeTokenResponseClient()) // it can alter authServer token to custom token;
                )
        );

        //resource
        http.oauth2ResourceServer(oauth2 -> oauth2
                .authenticationManagerResolver(authenticationManagerResolver)
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}