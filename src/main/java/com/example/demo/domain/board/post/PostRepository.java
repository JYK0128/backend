package com.example.demo.domain.board.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="post")
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByTag(String tag, Pageable pageable);
}
