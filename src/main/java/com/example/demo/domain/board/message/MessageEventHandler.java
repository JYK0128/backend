package com.example.demo.domain.board.message;

import javax.persistence.PreRemove;

public class MessageEventHandler {

    @PreRemove
    public void deleteMessage(Message message){
        message.setMessage(null);
        message.setMessage("deleted message");
    }
}
