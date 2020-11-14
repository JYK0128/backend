package com.example.demo.repository.board;

import com.example.demo.domain.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="post")
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByTag(String tag, Pageable pageable);

    @Override
    @Modifying
    @Query("delete from Post p where p.id = :#{#post.id}")
    void delete(Post post);
}
