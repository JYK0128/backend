package com.example.demo.etc;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.member.member.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExampleTest {
    PostRepository postRepository;

    @Autowired
    ExampleTest(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @SneakyThrows
    @Test
    void test1(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String time = objectMapper.writeValueAsString(LocalDateTime.now());
        LocalDateTime test = objectMapper.readValue(time, LocalDateTime.class);
        System.out.println("test: " + test);
    }

    @SneakyThrows
    @Test
    @Transactional
    void test2(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.registerModule(new Hibernate5Module()
                .enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING));
        String url = "{\n" +
                "    \"id\": 1,\n" +
                "    \"writer\": \"http://localhost/member/61\",\n" +
                "    \"tag\": \"test\",\n" +
                "    \"title\": \"test2\",\n" +
                "    \"contents\": \"test\",\n" +
                "    \"uploads\": [\n" +
                "        \"http://localhost/upload/1\"\n" +
                "    ]\n" +
                "}";

        //1. @RequestBody EntityModel -> url 작업으로 객체 생성.
        //2. custom 만들기 -> Hibernate proxy 필요
        //3.
        Post post = postRepository.getOne(1L);
        objectMapper.valueToTree(post);
        Member member = post.getWriter();
    }
}
