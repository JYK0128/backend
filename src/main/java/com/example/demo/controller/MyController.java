package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;


@Controller
public class MyController {
    @GetMapping(value = "{path}")
    String getPage(@PathVariable String path) {
        return path;
    }

    @GetMapping(value = {"favicon.ico", "**/favicon.ico"})
    @ResponseBody
    void returnNoFavicon() {
    }

    @GetMapping("/oauth2/code/google")
    @ResponseBody
    public Object getTokenWithGoogle(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }

    @GetMapping("/oauth2/code/naver")
    @ResponseBody
    public Object getTokenWithNaver(@RegisteredOAuth2AuthorizedClient("naver") OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }

    @GetMapping("/oauth2/code/kakao")
    @ResponseBody
    public Object getTokenWithKakao(@RegisteredOAuth2AuthorizedClient("kakao") OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }

    @GetMapping("/oauth2/userinfo")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal Principal principal) {
        if (principal != null) {
            return Map.of("name", principal.getName(), "authorities", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        }
        return null;
    }


}