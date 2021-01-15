package com.example.demo.domain.board.post;

import com.example.demo.domain.board.reply.Reply;

import javax.persistence.PreRemove;
import java.util.List;

public class PostEventHandler {

    @PreRemove
    public void preRemove(Post post){
        List<Reply> replies = post.getReplies();
        replies.forEach(m -> m.setPost(null));
    }
}
