package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;


@Controller
public class OauthController {
    OAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    OauthController(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @GetMapping("/oauth2/code/{authServer}")
    @ResponseBody
    private Object getToken(@PathVariable String authServer,
                            HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizeRequest authorizeRequest =
                OAuth2AuthorizeRequest.withClientRegistrationId(authServer)
                        .principal("anonymousUser")
                        .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        return authorizedClient.getAccessToken();
    }

    @GetMapping("/oauth2/userinfo")
    @ResponseBody
    public Object getUserInfo(@AuthenticationPrincipal Principal principal) {
        return principal;
    }
}