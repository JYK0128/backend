package com.example.demo.domain.board.post;


import com.example.demo.domain.board.message.MessageRepository;
import com.example.demo.domain.board.upload.Upload;
import com.example.demo.domain.board.upload.UploadRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@RepositoryRestController
public class PostController extends RepositoryRestExceptionHandler {
    private final EntityManager entityManager;
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;

    @Autowired
    PostController(MessageSource messageSource,
                   EntityManager entityManager,
                   PostRepository postRepository,
                   UploadRepository uploadRepository,
                   MessageRepository messageRepository) {
        super(messageSource);
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/post")
    public Object createPost(PersistentEntityResourceAssembler assembler,
                             @RequestBody EntityModel<Post> entityModel) throws JsonProcessingException {
        Post post = entityModel.getContent();
        if (post.getId() != null) throw new InvalidFormatException(null,
                "method 'Post' only use to create. " +
                        "if you modify this, use method 'Put' into correct path",
                post.getId(), Long.class);

        List<Upload> uploads = post.getUploads();
        for (Upload upload : uploads) upload.setPost(post);

        postRepository.save(post);
        return new ResponseEntity(assembler.toFullResource(post), HttpStatus.CREATED);
    }

    @PutMapping("/post/{id}")
    public Object updatePost(PersistentEntityResourceAssembler assembler,
                             @PathVariable Long id, @RequestBody EntityModel<Post> entityModel) throws IllegalAccessException, NoSuchFieldException {
        Post oldPost = postRepository.findById(id).get();
        Post newPost = entityModel.getContent();

        for (Field field : Post.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object oVal = field.get(oldPost);
            Object nVal = field.get(newPost);

            if (Collection.class.isAssignableFrom(field.getType())) {
                for(Object item :(Collection) nVal){
                    Field postField = item.getClass().getDeclaredField("post");
                    postField.setAccessible(true);
                    postField.set(item, newPost);
                }
            } else {
                if (nVal == null) field.set(newPost, oVal);
            }
        }

        newPost = postRepository.save(newPost);
        return new ResponseEntity(assembler.toFullResource(newPost), HttpStatus.OK);
    }
}