package com.example.demo.repository;

import com.example.demo.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "authority")
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
