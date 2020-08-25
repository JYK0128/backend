package com.example.demo.controller;

import com.example.demo.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path="/member")
public class AnonymousUserController {
    @Autowired
    MemberRepository memberRepository;

    @GetMapping(path = "/valid")
    public Object validUserName(@RequestParam(name="id") String username){
        HashMap<String, String> result = new HashMap<>();
        result.put("valid", String.valueOf(memberRepository.findByUsername(username).isEmpty()));
        return result;
    }
}
