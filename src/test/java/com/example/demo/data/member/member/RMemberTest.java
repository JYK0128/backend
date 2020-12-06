package com.example.demo.data.member.member;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.member.member.Member;
import com.example.demo.domain.member.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
                        .provider(OAuthServerProvider.values()[i % 3])
                        .build()
        ).collect(Collectors.toList());
        memberRepository.saveAll(members);
    }

    @Nested
    @Tag("query")
    class Query_that {
        @Test
        void findByEmailAndProvider() {
            String email = String.format(emailFormat, 1);
            OAuthServerProvider provider = OAuthServerProvider.values()[1];
            Optional<Member> member = memberRepository.findByEmailAndProvider(email, provider);
            assertThat(member).isNotEmpty();
        }

        @ParameterizedTest
        @EnumSource(OAuthServerProvider.class)
        void findAllByProvider(OAuthServerProvider provider) {
            int page = 1;
            int size = 10;
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Member> members = memberRepository.findAllByProvider(provider, pageRequest);

            assertThat(members).allSatisfy(m ->
                    assertEquals(provider, m.getProvider())
            );
        }
    }
}