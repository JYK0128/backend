package com.example.demo.config.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class JwtProperties {
    public static String HEADER_STRING = HttpHeaders.AUTHORIZATION;
    public static final int EXPIRATION_AFTER_DAYS = 10;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static SecretKey SECRET_KEY;
    //256bit
    private final String secretText = "test";

    @PostConstruct
    private void initialize () throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hashbytes = digest.digest(
                secretText.getBytes(StandardCharsets.UTF_8));

        SECRET_KEY = Keys.hmacShaKeyFor(hashbytes);
    }
}