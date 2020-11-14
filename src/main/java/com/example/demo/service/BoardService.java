package com.example.demo.service;

import com.example.demo.repository.board.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final PostRepository postRepository;

    @Autowired
    BoardService(PostRepository postRepository){
        this.postRepository = postRepository;
    }
}