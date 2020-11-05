package com.example.demo.data.member.member;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RMemberTest {
    final EntityManager entityManager;
    final MemberRepository memberRepository;

    final String emailFormat;
    final String nickNameFormat;

    @Autowired
    RMemberTest(EntityManager entityManager, MemberRepository memberRepository) {
        this.entityManager = entityManager;
        this.memberRepository = memberRepository;

        this.emailFormat = "test%d@test.com";
        this.nickNameFormat = "test%d";
    }

    @BeforeAll
    void setUp(){
        memberRepository.saveAll(
                IntStream.range(0, 100).mapToObj(i ->
                        Member.builder()
                                .email(String.format(emailFormat, i))
                                .nickname(String.format(nickNameFormat, i))
                                .providerType(ProviderType.values()[i%3])
                                .build()
                ).collect(Collectors.toList())
        );
        memberRepository.flush();
    }

    @Nested
    @Tag("constraint")
    class Constraint_that{
        @Test
        void id_is_primary(){
            Map<String, ArrayList<String>> annotations = new HashMap<>();
            for(Field field: Member.class.getDeclaredFields()){
                for(Annotation annotation:field.getDeclaredAnnotations()){
                    String aType = annotation.annotationType().getTypeName();
                    aType = aType.substring(aType.lastIndexOf(".")+1);

                    if(!annotations.containsKey(aType)) annotations.put(aType, new ArrayList<>());
                    annotations.get(aType).add(field.getName());
                }
            }

            assertTrue(annotations.get("Id").stream().allMatch(field -> field.equals("id")));
         }

        @Test
        void email_is_Unique() {
            Member member = Member.builder()
                    .email(String.format(emailFormat, 1))
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> memberRepository.save(member));
        }

        @Test
        void nickname_is_Unique() {
            Member member = Member.builder()
                    .nickname(String.format(nickNameFormat, 1))
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> memberRepository.save(member));
        }
    }

    @Nested
    @Tag("query")
    class Query_that{
        @Test
        void find_by_email() {
            Optional<Member> member = memberRepository.findByEmail(String.format(emailFormat, 1));
            assertTrue(member.isPresent());
        }

        @Test
        void find_all_by_providerType(){
            int page = 1;
            int size = 10;
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Member> members = memberRepository.findAllByProviderType(ProviderType.KAKAO, pageRequest);

            assertThat(members).allSatisfy(m ->
                assertTrue(m.getProviderType().equals(ProviderType.KAKAO))
            );
        }
    }
}
