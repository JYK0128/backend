package com.example.demo.syntax;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.example.demo.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

//https://ramees.tistory.com/36
@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class PersistenceTests {
    private final DataSource dataSource;
    private final MemberRepository memberRepository;
    private Member member1;
    private Member member2;

    @Autowired
    PersistenceTests(MemberRepository memberRepository, DataSource dataSource) {
        this.memberRepository = memberRepository;
        this.dataSource = dataSource;
    }

    @BeforeAll
    void init(){
        this.member1 = memberRepository.saveAndFlush(Member.builder()
                .email("test1@test.com")
                .nickname("Origin1")
                .provider(ProviderType.KAKAO)
                .build()
        );
        this.member2 = Member.builder()
                .email("test2@test.com")
                .nickname("Origin2")
                .provider(ProviderType.KAKAO)
                .build();
    }

    @Test
    void relation_of_persistence_and_entity_object_test1() throws SQLException {
        member1.setNickname("Fake1");

        Member test = memberRepository.findById(member1.getId()).orElseGet(null);
        assertEquals(test.getNickname(), "Origin1");

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(String.format("select nickname from member where id=%d", member1.getId()));

        System.out.println("Start: Searching in JDBC");
        while (rs.next()) {
            String text = rs.getString(1);
            System.out.println("nickname = " + text);
        }
        System.out.println("Finish: Searching in JDBC");
        conn.close();
    }

    @Test
    void relation_of_persistence_and_entity_object_test2() throws SQLException {
        member2 = memberRepository.saveAndFlush(member2);
//        member2.setNickname("Fake2");
//
//        Member test = memberRepository.findById(member2.getId()).orElseGet(null);
//        assertNotEquals(test.getNickname(), "Origin2");

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(String.format("select nickname from member where id=%d", member2.getId()));

        System.out.println("Start: Searching in JDBC");
        while (rs.next()) {
            String text = rs.getString(1);
            System.out.println("nickname = " + text);
        }
        System.out.println("Finish: Searching in JDBC");
        conn.close();
    }
}