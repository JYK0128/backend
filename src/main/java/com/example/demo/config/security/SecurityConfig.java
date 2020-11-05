package com.example.demo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private AuthenticationManagerResolver resolver;

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
//        http.addFilter(Filter);
    }

    private void authorizationConfig(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index", "/oauth2/**").permitAll()
                .antMatchers("/login-success").hasAnyAuthority("SCOPE_openid")
                .anyRequest().authenticated();
    }

    private void oAuthConfig(HttpSecurity http) throws Exception {
        //client
        http.oauth2Client(oauth2 -> oauth2
                .clientRegistrationRepository(clientRegistrationRepository)
                .authorizedClientService(authorizedClientService) // or authorizedClientRepository
                .authorizationCodeGrant(codeGrant -> codeGrant
                        .authorizationRequestResolver(new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")) // or authorizationRequestRepository
                        .accessTokenResponseClient(new DefaultAuthorizationCodeTokenResponseClient())
                )
        );

        //resource
        http.oauth2ResourceServer(oauth2 -> oauth2
                .authenticationManagerResolver(resolver)
        );
    }
}