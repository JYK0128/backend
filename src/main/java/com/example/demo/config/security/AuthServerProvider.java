package com.example.demo.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.regex.Pattern;

public enum AuthServerProvider {
    GOOGLE {
        @Override
        public ClientRegistration getServer() {
            return ClientRegistration.withRegistrationId("google")
                    .clientName("Google")
                    .clientId("278564592334-j2vlvrr7lc06tfglindqf564tg75h0qt.apps.googleusercontent.com")
                    .clientSecret("UF_Crv3IRVPtZLm7DdVEggzs")
                    .scope(new String[]{"openid", "profile", "email"})
                    .redirectUriTemplate("{baseUrl}/oauth2/code/{registrationId}")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)

                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .userNameAttributeName("sub")
                    .build();
        }
    },
    KAKAO {
        @Override
        public ClientRegistration getServer() {
            return ClientRegistration.withRegistrationId("kakao")
                    .clientName("Kakao")
                    .clientId("6d70a01b8b65ab91b4ecfaea251c1422")
                    .clientSecret("c8bNweg3AF2xMkX5xmhHaCikplsjVcqm")
                    .scope(new String[]{"profile", "account_email"})
                    .redirectUriTemplate("{baseUrl}/oauth2/code/{registrationId}")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)

                    .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                    .tokenUri("https://kauth.kakao.com/oauth/token")
                    .userInfoUri("https://kapi.kakao.com/v2/user/me")
                    .userNameAttributeName("kakao_account")
                    .build();
        }
    },
    NAVER {
        @Override
        public ClientRegistration getServer() {
            return ClientRegistration.withRegistrationId("naver")
                    .clientName("Naver")
                    .clientId("KbUfgrZHHcyINOOffQlU")
                    .clientSecret("678P53KC0p")
                    .scope(new String[]{"name", "email", "openid"})
                    .redirectUriTemplate("{baseUrl}/oauth2/code/{registrationId}")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)

                    .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                    .tokenUri("https://nid.naver.com/oauth2.0/token")
                    .userInfoUri("https://openapi.naver.com/v1/nid/me")
                    .userNameAttributeName("response")
                    .build();
        }
    };


    public abstract ClientRegistration getServer();
}
