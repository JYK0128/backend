package com.example.demo.repository.board;

import com.example.demo.domain.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/board/post")
public interface PostRepository extends JpaRepository<Post, Long> {
}
