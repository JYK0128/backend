package com.example.demo.config.security;

import com.example.demo.domain.member.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
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
@EnableJpaAuditing
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
                .antMatchers("/", "/favicon.ico", "/oauth2/**", "/docs/**", "/profile/**").permitAll()
                .antMatchers(HttpMethod.GET,"/post/**", "/message/**", "/upload/**").permitAll()
                .antMatchers("/member/**", "/post/**", "/message/**", "/upload/**").hasAuthority(Member.AUTHORITY)
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

        configuration.addAllowedOrigin("http://localhost"); // if setAllowCredentials True, * not allowed
//        configuration.addAllowedOrigin("https://api.jyworld.tk"); // if setAllowCredentials True, * not allowed
        configuration.addAllowedOrigin("*"); // if setAllowCredentials True, * not allowed
        configuration.setAllowCredentials(false);
        //        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("Access-Control-Allow-Headers");
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Origin");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("x-Requested-with");
        configuration.addExposedHeader("Accept");
        configuration.addExposedHeader("Accept-Encoding");
        configuration.addExposedHeader("X-CSRF-Token");



        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuditorAware<Member> auditorProvider() {
        return new SecurityAuditorAware();
    }
}