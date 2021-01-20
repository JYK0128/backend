package com.example.demo.domain.board.post;

import com.example.demo.domain.board.reply.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select p.replies from Post p join p.replies r where p.id = :id and r.topic is null",
            countQuery = "select count(1) from Post p join p.replies r where p.id = :id and r.topic is null"
    )
    Page<Reply> findAllRepliesById(Long id, Pageable pageable);
}
