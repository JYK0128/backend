package com.example.demo.repository.member;

import com.example.demo.domain.member.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "authority")
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
