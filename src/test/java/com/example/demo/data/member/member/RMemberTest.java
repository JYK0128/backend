package com.example.demo.data.member.member;

import com.example.demo.config.security.OAuthProvider;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RMemberTest {
    final String emailFormat = "test%d@test.com";

    final EntityManagerFactory entityManagerFactory;
    final MemberRepository memberRepository;

    @Autowired
    RMemberTest(EntityManagerFactory entityManagerFactory, MemberRepository memberRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    void setUp() {
        List<Member> members = IntStream.range(0, 10).mapToObj(i ->
                Member.builder()
                        .email(String.format(emailFormat, i))
                        .provider(OAuthProvider.values()[i % 3])
                        .build()
        ).collect(Collectors.toList());
        memberRepository.saveAll(members);
    }

    @Nested
    @Tag("query")
    class Query_that {

    }
}