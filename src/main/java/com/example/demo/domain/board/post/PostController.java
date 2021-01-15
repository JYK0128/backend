package com.example.demo.domain.board.post;


import com.example.demo.domain.board.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.annotation.LastModifiedDate;
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
import java.time.LocalDateTime;
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

    @PostMapping("/post")
    public Object createPost(PersistentEntityResourceAssembler assembler, Principal principal,
                             @RequestBody EntityModel<Post> entityModel) {
        Post post = entityModel.getContent();
        Assert.isTrue(post.isCreatable(), "post is not creatable.");

        List<Upload> uploads = post.getUploads();
        for (Upload upload : uploads) upload.setPost(post);

        postRepository.save(post);
        return new ResponseEntity(assembler.toFullResource(post), HttpStatus.CREATED);
    }

    @GetMapping("/post/{id}")
    public Object readPost(PersistentEntityResourceAssembler assembler, @PathVariable Long id){
        postRepository.increaseView(id);
        Post post = postRepository.findById(id).get();
        return new ResponseEntity(assembler.toFullResource(post), HttpStatus.OK);
    }

    @PutMapping("/post/{id}")
    public Object updatePost(PersistentEntityResourceAssembler assembler, Principal principal,
                             @PathVariable Long id, @RequestBody EntityModel<Post> entityModel) throws IllegalAccessException {
        Post newPost = entityModel.getContent();
        Post oldPost = postRepository.findById(id).get();
        Assert.isTrue(newPost.isUpdatable(oldPost, principal), "post is not updatable.");

        for (Field field : Post.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object oVal = field.get(oldPost);
            Object nVal = field.get(newPost);

            if (Collection.class.isAssignableFrom(field.getType())) {
                if(field.getName().equals("uploads")){
                    field.set(newPost, nVal);
                }else{
                    field.set(newPost, oVal);
                }
            } else {
                if (field.getAnnotation(LastModifiedDate.class) == null) {
                    if (nVal == null) field.set(newPost, oVal);
                }else{
                    field.set(newPost, LocalDateTime.now());
                }
            }
        }

        postRepository.save(newPost);
        return new ResponseEntity(assembler.toFullResource(newPost), HttpStatus.OK);
    }

    @DeleteMapping({"/post/{id}"})
    public Object deletePost(@PathVariable Long id, Principal principal) {
        Post post = postRepository.findById(id).get();
        Assert.isTrue(post.isDeletable(principal), "post is not deletable.");

        postRepository.delete(post);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}