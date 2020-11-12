package com.example.demo.config.security;

import com.example.demo.domain.member.Member;
import com.example.demo.repository.member.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class OauthUserService {
    MemberRepository memberRepository;

    @Autowired
    OauthUserService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Authentication authenticate(Authentication authentication) {
        try {
            UserDetails userDetails = this.loadUserByAuthentication(authentication);
            final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            return new UsernamePasswordAuthenticationToken(userDetails, authentication.getPrincipal(), authorities);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserDetails loadUserByAuthentication(Authentication authentication) throws URISyntaxException {
        String token = authentication.getPrincipal().toString();
        OAuthServerProvider provider = getProvider(token);
        URI userInfoUri = new URI(provider.getUserInfoUri());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate
                .exchange(userInfoUri, HttpMethod.GET, request, JsonNode.class);
        JsonNode userInfo = response.getBody();

        String email = userInfo.findPath("email").asText();
        memberRepository.findByEmail(email).orElseGet(() ->
                memberRepository.save(Member.builder().email(email).provider(provider).build()));

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("SCOPE_openid");

        return User.builder()
                .username(email)
                .password(token)
                .authorities(grantedAuthorities)
                .build();
    }

    private static OAuthServerProvider getProvider(String token){
        if (token.startsWith("ya29.")) return OAuthServerProvider.GOOGLE;
        else if(token.startsWith("AAAAO")) return OAuthServerProvider.NAVER;
        else if(Pattern.matches("^.{43}AAAF1.{6}$", token)) return OAuthServerProvider.KAKAO;
        else return null;
    }
}