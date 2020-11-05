package com.example.demo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration
public class OauthConfig {
    private static List<String> clients = Arrays.asList("google", "naver", "kakao");

    @Autowired
    MyAuthenticationProvider authenticationProvider;

    //Client Service
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }

    //Client Repository
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = Arrays.stream(AuthServerProvider.values())
                .map(authServer -> authServer.getServer())
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    //AuthenticationManager Resolver
    @Bean
    AuthenticationManagerResolver<HttpServletRequest> resolver() {
        return request -> authenticationProvider::authenticate;
    }

    private String getIssuer(String token){
        if (token.startsWith("ya29")) return "google";
        else if(token.startsWith("AAAA")) return "naver";
        else if(Pattern.matches("^.{43}AAAF1L.{5}$", token)) return "kakao";
        else return null;
    }
}