package com.example.demo.service;

import com.example.demo.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final MemberRepository memberRepository;

    @Autowired
    UserService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
}
