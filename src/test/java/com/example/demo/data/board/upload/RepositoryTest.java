package com.example.demo.data.board.upload;

import com.example.demo.domain.board.Post;
import com.example.demo.domain.board.Upload;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.board.PostRepository;
import com.example.demo.repository.board.UploadRepository;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RUploadTest {
    final EntityManager entityManager;
    final MemberRepository memberRepository;
    final PostRepository postRepository;
    final UploadRepository uploadRepository;

    final String oriNameFormat;


    @Autowired
    RUploadTest(EntityManager entityManager,
                MemberRepository memberRepository,
                PostRepository postRepository,
                UploadRepository uploadRepository) {
        this.entityManager = entityManager;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;

        this.oriNameFormat = "testfile%d";
    }

    @BeforeAll
    void setUp(){
        Member member = memberRepository.save(new Member());
        Post post = postRepository.save(Post.builder().member(member).build());
        uploadRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Upload.builder()
                                .oriName(String.format(oriNameFormat, i))
                                .uuid(UUID.randomUUID().toString())
                                .post(post)
                                .build()
                ).collect(Collectors.toList())
        );
    }

    @Nested
    @Tag("query")
    class Query_that{

    }
}