package com.example.demo.domain.board.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;


@RepositoryRestResource(path="post")
public interface PostRepository extends JpaRepository<Post, Long> {
    @Modifying
    @Transactional
    @RestResource(exported = false)
    @Query("update Post p set p.views = p.views + 1 where p.id = :id")
    void increaseView(Long id);

    @Query("select count(p.replies) from Post p where p.id = :id")
    Long countComments(Long id);
}
