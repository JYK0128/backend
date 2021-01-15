package com.example.demo.config.security;

import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

@Service
public class OAuthAuthenticationManager implements AuthenticationManager {
    MemberRepository memberRepository;

    @Autowired
    OAuthAuthenticationManager(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            UserDetails userDetails = this.loadUserByAuthentication(authentication);
            final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), authorities);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserDetails loadUserByAuthentication(Authentication authentication) throws URISyntaxException {
        String token = authentication.getPrincipal().toString();
        OAuthProvider provider = OAuthProvider.getProvider(token);
        URI userInfoUri = new URI(provider.getUserInfoUri());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate
                .exchange(userInfoUri, HttpMethod.GET, request, JsonNode.class);
        JsonNode userInfo = response.getBody();

        String email = userInfo.findPath("email").asText();
        Member member = memberRepository.findByEmailAndProvider(email, provider).orElseGet(() ->
                memberRepository.save(Member.builder().email(email).provider(provider).build()));
        return member;
    }
}