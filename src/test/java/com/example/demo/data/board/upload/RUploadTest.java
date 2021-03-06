package com.example.demo.data.board.upload;

import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RUploadTest {
    private final EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;

    private final  String oriNameFormat;


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
        Post post = postRepository.save(Post.builder().writer(member).build());
        uploadRepository.saveAll(
                IntStream.range(0, 10).mapToObj(i ->
                        Upload.builder()
                                .filename(String.format(oriNameFormat, i))
//                                .post(post)
                                .build()
                ).collect(Collectors.toList())
        );
    }

    @Nested
    @Tag("query")
    class Query_that{

    }
}