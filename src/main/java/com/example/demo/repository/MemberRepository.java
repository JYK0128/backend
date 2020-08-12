package com.example.demo.repository;


import com.example.demo.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(path="member")
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m join fetch m.role r join fetch r.authorities where m.username = ?1")
    public Optional<Member> findByUsername(String username);
}