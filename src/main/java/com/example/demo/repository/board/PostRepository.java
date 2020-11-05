package com.example.demo.repository.board;

import com.example.demo.domain.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="post")
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByTag(String tag, Pageable pageable);
}
