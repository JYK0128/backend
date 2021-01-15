package com.example.demo.domain.member.member;

import com.example.demo.config.security.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "member")
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndProvider(String email, OAuthProvider provider);

    @Override
    @Modifying
    @Query("delete from Member m where m.id = :#{#member.id}")  // native cascade
    void delete(Member member);
}