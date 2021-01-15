package com.example.demo.domain.board.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "reply")
public interface ReplyRepository extends JpaRepository<Reply, Long> {

}