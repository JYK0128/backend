package com.example.demo.business;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MemberServiceTests {

    private final MemberRepository memberRepository;
    private final Member member;

    @Autowired
    MemberServiceTests(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.member = Member.builder()
                .email("test@test.com")
                .provider(OAuthServerProvider.KAKAO)
                .build();
    }

    @Test
    void signUp() {
        //given
        Member newMember = member;
        //when
        memberRepository.save(newMember);
        //then

        assertThat(memberRepository.findOne(Example.of(newMember))).isPresent();
    }

    @Test
    void leave() {
        //given
        Member member = this.member;
        memberRepository.save(member);
        //when
        memberRepository.delete(member);
        //then
        assertThat(memberRepository.findOne(Example.of(member))).isEmpty();
    }
}