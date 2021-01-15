package com.example.demo.data.board.post;

import com.example.demo.domain.board.reply.Reply;
import com.example.demo.domain.board.reply.ReplyRepository;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.board.upload.UploadRepository;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RPostTest {
    private final EntityManager entityManager;
    private final UploadRepository uploadRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;


    @Autowired
    RPostTest(EntityManager entityManager,
              UploadRepository uploadRepository,
              MemberRepository memberRepository,
              ReplyRepository replyRepository,
              PostRepository postRepository) {
        this.entityManager = entityManager;
        this.uploadRepository = uploadRepository;
        this.memberRepository = memberRepository;
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
    }

    @BeforeAll
    void setUp() {
        Member member = memberRepository.save(new Member());
        Member replier = memberRepository.save(new Member());

        List<Post> posts = postRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Post.builder()
                                .tag("tag" + (i % 3))
                                .title("title" + i)
                                .createDate(LocalDateTime.now())
                                .views((long) i)
                                .content("content" + i)

                                .writer(member)
                                .build()
                ).collect(Collectors.toList()));

        List<Upload> uploads = uploadRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Upload.builder()
                                .filename("file" + i + ".txt")
                                .post(posts.get(i))
                                .build()
                ).collect(Collectors.toList()));

        List<Reply> messages = replyRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Reply.builder()
                                .post(posts.get(i))
                                .writer(replier)
                                .build()
                ).collect(Collectors.toList()));
    }

    @Nested
    @Tag("query")
    class Query_that {

    }
}
