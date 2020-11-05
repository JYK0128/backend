package com.example.demo.repository.board;

import com.example.demo.domain.board.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "message")
public interface MessageRepository extends JpaRepository<Message, Long> {

}
