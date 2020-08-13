package com.example.demo.service;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Member;
import com.example.demo.domain.Role;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ApplicationUserService implements UserDetailsService {
    @Autowired
    MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow();
//                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
        Collection<? extends GrantedAuthority> grantedAuthorities = getAuthorities(Set.of(member.getRole()));
        UserDetails userDetails = User.withUserDetails(member).authorities(grantedAuthorities).build();
        return userDetails;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        Stream<String> role_stream = roles.stream().map(role -> role.getAuthority());
        Stream<String> privilege_stream = roles.stream().flatMap(role -> role.getAuthorities().stream()).map(authority -> authority.getAuthority());
        Stream<String> concat_stream = Stream.concat(role_stream, privilege_stream);
        return concat_stream.map(privilege -> new SimpleGrantedAuthority(privilege)).collect(Collectors.toSet());
    }
}