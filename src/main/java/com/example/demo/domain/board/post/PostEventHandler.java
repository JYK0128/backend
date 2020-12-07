package com.example.demo.domain.board.post;

import com.example.demo.domain.board.message.Message;

import javax.persistence.PreRemove;
import java.util.List;

public class PostEventHandler {

    @PreRemove
    public void preRemove(Post post){
        List<Message> messages = post.getMessages();
        messages.forEach(m -> m.setPost(null));
    }
}
