package com.example.demo.repository.member;


import com.example.demo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path="member")
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m join fetch m.role r join fetch r.authorities where m.username = ?1")
    public Optional<Member> findByUsername(String username);
}