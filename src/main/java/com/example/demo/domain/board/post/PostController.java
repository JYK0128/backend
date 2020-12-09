package com.example.demo.domain.board.post;


import com.example.demo.domain.board.upload.Upload;
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

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@RepositoryRestController
public class PostController extends RepositoryRestExceptionHandler {
    private final PostRepository postRepository;

    @Autowired
    PostController(MessageSource messageSource,
                   PostRepository postRepository) {
        super(messageSource);
        this.postRepository = postRepository;
    }

    @Transactional
    @PostMapping("/post")
    public Object createPost(PersistentEntityResourceAssembler assembler,
                             @RequestBody EntityModel<Post> entityModel) {
        Post post = entityModel.getContent();
        Assert.isNull(post.getId(), "id must be null");

        List<Upload> uploads = post.getUploads();
        for (Upload upload : uploads) upload.setPost(post);

        postRepository.save(post);
        return new ResponseEntity(assembler.toFullResource(post), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping("/post/{id}")
    public Object readPost(PersistentEntityResourceAssembler assembler, @PathVariable Long id){
        postRepository.readById(id);
        Post post = postRepository.findById(id).get();
        return new ResponseEntity(assembler.toFullResource(post), HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/post/{id}")
    public Object updatePost(PersistentEntityResourceAssembler assembler,
                             @PathVariable Long id, @RequestBody EntityModel<Post> entityModel) throws IllegalAccessException, NoSuchFieldException {
        Post oldPost = postRepository.findById(id).get();
        Post newPost = entityModel.getContent();
        Assert.isNull(newPost.getId(), "id must be null");

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

        postRepository.save(newPost);
        return new ResponseEntity(assembler.toFullResource(newPost), HttpStatus.OK);
    }
}