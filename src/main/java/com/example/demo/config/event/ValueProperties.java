package com.example.demo.config.event;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Role;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Transactional
public class ValueProperties {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthorityRepository authorityRepository;

    public static Role USER_ROLE;

    @PostConstruct
    public void initialize(){
        Authority read_user = Authority.builder().target("USER").permission(Authority.PERMISSION_TYPE.READ).build();
        Authority user_authority = authorityRepository.findOne(Example.of(read_user)).orElseGet(() -> authorityRepository.saveAndFlush(read_user));

        Role userRole = Role.builder().name("USER").authority(user_authority).build();
        this.USER_ROLE = roleRepository.findOne(Example.of(userRole)).orElseGet(()->roleRepository.saveAndFlush(userRole));
    }
}
