package com.example.demo.domain.board.message;


import com.example.demo.domain.board.post.Post;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RepositoryRestController
public class MessageController extends RepositoryRestExceptionHandler {
    private final MessageRepository messageRepository;

    @Autowired
    MessageController(MessageSource messageSource,
                      MessageRepository messageRepository) {
        super(messageSource);
        this.messageRepository = messageRepository;
    }

    @PostMapping(value = {"/message"})
    public Object createMessage(PersistentEntityResourceAssembler assembler,
                                @RequestBody EntityModel<Message> entityModel){
        Message message = entityModel.getContent();
        Assert.isNull(message.getId(), "id must be null");
        Assert.notNull(message.getPost(), "post must not be null");

        Post post = message.getPost();
        message.setPost(post);
        Message topic = message.getTopic(); //
        message.setTopic(topic);

        messageRepository.save(message);
        return new ResponseEntity(assembler.toFullResource(message), HttpStatus.CREATED);
    }

    @PutMapping("/message/{id}")
    public Object updateMessage(PersistentEntityResourceAssembler assembler,
                                @PathVariable Long id,
                                @RequestBody EntityModel<Message> entityModel) throws IllegalAccessException {
        Message oldMessage = messageRepository.findById(id).get();
        Message newMessage = entityModel.getContent();
        Assert.notNull(newMessage.getId(), "id must be null");
        Assert.notNull(newMessage.getPost(), "post must be null");

        for (Field field : Message.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object oVal = field.get(oldMessage);
            Object nVal = field.get(newMessage);

            if (Collection.class.isAssignableFrom(field.getType())) {
                field.set(newMessage, oVal);
            } else {
                if (nVal == null) field.set(newMessage, oVal);
            }
        }

        messageRepository.save(newMessage);
        return new ResponseEntity(assembler.toFullResource(newMessage), HttpStatus.OK);
    }

    @DeleteMapping({"/message/{id}"})
    public Object deleteMessage(@PathVariable Long id) {
        List<Message> batch = new ArrayList<>();
        Message message = messageRepository.findById(id).get(); //

        if(message.getReplies().isEmpty()) {
            batch.add(message);
        }else {
            message.setWriter(null);
            message.setMessage("deleted message");
            messageRepository.save(message);
        }

        Message topic = message.getTopic();
        while(topic.getWriter() == null && CollectionUtils
                .isEqualCollection(topic.getReplies(), Collections.singleton(message))) {
            batch.add(topic);
            message = topic;
            topic = topic.getTopic();
        }

        messageRepository.deleteInBatch(batch);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}