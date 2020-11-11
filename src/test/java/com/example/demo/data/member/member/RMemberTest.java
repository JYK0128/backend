package com.example.demo.data.member.member;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManagerFactory;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RMemberTest {
    final String emailFormat = "test%d@test.com";
    final String nickNameFormat = "test%d";

    final EntityManagerFactory entityManagerFactory;
    final MemberRepository memberRepository;

    @Autowired
    RMemberTest(EntityManagerFactory entityManagerFactory, MemberRepository memberRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    void setUp() {
        memberRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Member.builder()
                                .email(String.format(emailFormat, i))
                                .nickname(String.format(nickNameFormat, i))
                                .provider(ProviderType.values()[i % 3])
                                .build()
                ).collect(Collectors.toList())
        );
    }

    @Nested
    @Tag("query")
    class Query_that {
        @Test
        void find_by_email() {
            Optional<Member> member = memberRepository.findByEmail(String.format(emailFormat, 1));
            assertTrue(member.isPresent());
        }

        @ParameterizedTest
        @EnumSource(ProviderType.class)
        void find_all_by_provider(ProviderType providerType) {
            int page = 1;
            int size = 10;
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Member> members = memberRepository.findAllByProvider(providerType, pageRequest);

            assertThat(members).allSatisfy(m ->
                    assertEquals(providerType, m.getProvider())
            );
        }
    }
}