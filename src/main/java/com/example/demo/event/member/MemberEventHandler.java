package com.example.demo.event.member;

import com.example.demo.config.event.ValueProperties;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Authority;
import com.example.demo.domain.member.Role;
import com.example.demo.repository.member.AuthorityRepository;
import com.example.demo.repository.member.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PrePersist;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
@Import(ValueProperties.class)
public class MemberEventHandler {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PrePersist
    public void onPrePersist(Member member){
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        var ref = new Object() {Role memberRole = member.getRole();};

        if(ref.memberRole != null){
            Set<Authority> authorites = ref.memberRole.getAuthorities();
            authorites = authorites.stream()
                    .map(authority -> authorityRepository.findOne(Example.of(authority))
                    .orElseGet(()-> authorityRepository.save(authority)))
                    .collect(Collectors.toSet());
            authorityRepository.flush();

            ref.memberRole.setAuthorities(authorites);
            ref.memberRole = roleRepository.findOne(Example.of(ref.memberRole)).orElseGet(()->roleRepository.saveAndFlush(ref.memberRole));
        }else{
            ref.memberRole = ValueProperties.USER_ROLE;
        }
        member.setRole(ref.memberRole);
    }
}