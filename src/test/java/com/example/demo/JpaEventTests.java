package com.example.demo;

import com.example.demo.domain.Member;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;


//@DataJpaTest
//@Import({PasswordConfiguration.class, DefaultProperties.class})
@SpringBootTest
public class JpaEventTests {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthorityRepository authorityRepository;

/*    @Test
    void event_test1(){
        Authority[] authorities = {
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.READ).build(),
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.WRITE).build(),
        };
        authorityRepository.save(authorities[0]);
        authorityRepository.save(authorities[1]);
        authorityRepository.save(authorities[0]);

        System.out.println(authorityRepository.findAll());
    }*/

/*    @Test
    void event_test2(){
        Authority[] authorities = {
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.READ).build(),
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.WRITE).build(),
        };
        Role[] roles = {
                Role.builder().name("test0").authorities(Set.of(authorities)).build(),
                Role.builder().name("test1").authorities(Set.of(authorities)).build()
        };

        roleRepository.save(roles[0]);
        roleRepository.save(roles[1]);
        Role test = roleRepository.findOne(Example.of(roles[0])).orElseGet(()->roleRepository.save(roles[0]));

        System.out.println(roleRepository.findAll());
    }*/

/*    @Test
    void test3(){
        Authority[] authorities = {
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.READ).build(),
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.WRITE).build(),
        };
        authorityRepository.saveAll(Set.of(authorities));
        authorityRepository.flush();

        Role[] roles = {
                Role.builder().name("test0").authorities(Set.of(authorities)).build(),
                Role.builder().name("test1").authorities(Set.of(authorities)).build()
        };
        roleRepository.saveAll(Set.of(roles));
        roleRepository.flush();

        Member[] members = {
                Member.builder().username("test1").password("test").role(roles[0]).build(),
                Member.builder().username("test2").password("test").role(roles[1]).build()
        };
        memberRepository.saveAll(Set.of(members));
        memberRepository.flush();

        Member a = memberRepository.findByUsername("test1").orElse(null);
        System.out.println(a.getRole().getName());
    }*/

/*    @Test
    void test4(){
        Authority[] authorities = {
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.READ).build(),
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.WRITE).build(),
        };

        Authority[] authorities2 = {
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.READ).build(),
                Authority.builder().target("test").permission(Authority.PERMISSION_TYPE.WRITE).build(),
        };

        Role[] roles = {
                Role.builder().name("test0").authorities(Set.of(authorities)).build(),
                Role.builder().name("test1").authorities(Set.of(authorities)).build(),
                Role.builder().name("test0").authorities(Set.of(authorities2)).build()
        };

        Member[] members = {
                Member.builder().username("test1").password("test").role(roles[0]).build(),
                Member.builder().username("test2").password("test").build(),
                Member.builder().username("test3").password("test").role(roles[2]).build(),
        };

        System.out.println("testttttttttttttt");
        List<Member> test = List.of(members).stream()
                .map(member -> memberRepository.findByUsername(member.getUsername()).orElseGet(() -> memberRepository.save(member)))
                .collect(Collectors.toList());
        System.out.println(test);
    }*/

    @Test
    @Transactional
    void test5(){
        Member member = Member.builder().username("user").password("user").build();
        memberRepository.findByUsername("user").orElseThrow();
        System.out.println(member);
    }

    @Autowired
    ApplicationUserService applicationUserService;

    @Test
    @Transactional
    void test6(){
        UserDetails member = applicationUserService.loadUserByUsername("user");
        System.out.println("testtttttttttttt" + member.toString());
        assertNotNull(member);
    }
}
