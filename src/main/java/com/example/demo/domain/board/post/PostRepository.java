package com.example.demo.domain.board.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

@RepositoryRestResource(path="post")
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByTag(String tag, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Post p set p.views = p.views + 1 where p.id = :id")
    void readById(Long id);
}
