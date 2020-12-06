package com.example.demo.domain.board.post;

import com.example.demo.domain.board.message.Message;

import javax.persistence.PreRemove;

public class PostEventHandler {

    @PreRemove
    public void preRemove(Post post){
        for (Message message : post.getMessages()) {
            message.setPost(null);
        }
    }
}
