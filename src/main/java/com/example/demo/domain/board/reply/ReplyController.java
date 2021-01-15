package com.example.demo.domain.board.reply;


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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RepositoryRestController
public class ReplyController extends RepositoryRestExceptionHandler {
    private final ReplyRepository replyRepository;

    @Autowired
    ReplyController(MessageSource messageSource,
                    ReplyRepository replyRepository) {
        super(messageSource);
        this.replyRepository = replyRepository;
    }

    @PostMapping(value = {"/message"})
    public Object createMessage(PersistentEntityResourceAssembler assembler,
                                @RequestBody EntityModel<Reply> entityModel){
        Reply message = entityModel.getContent();
        Assert.isTrue(message.isCreatable(), "message is not creatable.");

        Post post = message.getPost();
        message.setPost(post);
        Reply topic = message.getTopic();
        message.setTopic(topic);

        replyRepository.save(message);
        return new ResponseEntity(assembler.toFullResource(message), HttpStatus.CREATED);
    }

    @PutMapping("/message/{id}")
    public Object updateMessage(PersistentEntityResourceAssembler assembler, Principal principal,
                                @PathVariable Long id,
                                @RequestBody EntityModel<Reply> entityModel) throws IllegalAccessException {
        Reply newMessage = entityModel.getContent();
        Reply oldMessage = replyRepository.findById(id).get();
        Assert.isTrue(newMessage.isUpdatable(oldMessage, principal), "message is not updatable.");

        for (Field field : Reply.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object oVal = field.get(oldMessage);
            Object nVal = field.get(newMessage);

            if (Collection.class.isAssignableFrom(field.getType())) {
                field.set(newMessage, oVal);
            } else {
                if (nVal == null) field.set(newMessage, oVal);
            }
        }

        replyRepository.save(newMessage);
        return new ResponseEntity(assembler.toFullResource(newMessage), HttpStatus.OK);
    }

    @DeleteMapping({"/message/{id}"})
    public Object deleteMessage(@PathVariable Long id, Principal principal) {
        Reply message = replyRepository.findById(id).get();
        Assert.isTrue(message.isDeletable(principal), "message is not deletable.");

        List<Reply> batch = new ArrayList<>();
        if(message.getReplies().isEmpty()) {
            batch.add(message);
        }else {
            message.setWriter(null);
            message.setMessage("deleted message");
            replyRepository.save(message);
        }

        Reply topic = message.getTopic();
        while(topic != null && topic.getWriter() == null &&
                CollectionUtils.isEqualCollection(topic.getReplies(), Collections.singleton(message))) {
            batch.add(topic);
            message = topic;
            topic = topic.getTopic();
        }

        replyRepository.deleteInBatch(batch);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}