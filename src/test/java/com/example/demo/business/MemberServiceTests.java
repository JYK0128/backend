package com.example.demo.business;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.board.post.Post;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberServiceTests {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final EntityManager entityManager;

    Member.MemberBuilder preDefinedMemberBuilder;

    @Autowired
    MemberServiceTests(MemberRepository memberRepository, PostRepository postRepository,
                       EntityManager entityManager) {
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    void setUp(){
        preDefinedMemberBuilder = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.KAKAO);
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    void signUp() {
        Member member = preDefinedMemberBuilder.build();
        memberRepository.save(member);

        assertThat(entityManager.contains(member)).isTrue();
    }

    @Test
    @Order(997)
    @Rollback(value = false)
    void beforeLeave(){
        Member member = memberRepository.findById((long) 1).get();
        Post post = Post.builder().writer(member).tag("test").title("title").contents("content").build();
        postRepository.save(post);

        assertThat(entityManager.contains(member)).isTrue();
        assertThat(member.getPosts().get(0)).isEqualTo(post);
    }


    @Test
    @Order(998)
    @Rollback(value = false)
    void leave() {
        Member member = memberRepository.findById((long) 1).get();
        memberRepository.delete(member);
    }

    @Test
    @Order(999)
    @Rollback(value = false)
    void afterLeave() {
        assertThat(memberRepository.findAll()).isEmpty();
        assertThat(postRepository.findAll()).isEmpty();
    }
}