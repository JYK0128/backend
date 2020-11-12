package com.example.demo.repository.member;

import com.example.demo.config.security.OAuthServerProvider;
import com.example.demo.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "member")
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Page<Member> findAllByProvider(OAuthServerProvider provider, Pageable pageable);
}