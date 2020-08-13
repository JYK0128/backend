package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/member")
public class AnonymousUserController {
    @Autowired
    MemberRepository memberRepository;

    @GetMapping(path = "/search")
    public Object validateUserName(@RequestParam(name="id") String username){
        HashMap<String, String> result = new HashMap<>();
        result.put("id", username);
        return result;
    }
}
