package com.example.demo.domain.member.member;


import com.example.demo.domain.board.message.MessageRepository;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;

@RepositoryRestController
public class MemberController extends RepositoryRestExceptionHandler {
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;

    @Autowired
    MemberController(MessageSource messageSource,
                     PostRepository postRepository,
                     UploadRepository uploadRepository,
                     MessageRepository messageRepository) {
        super(messageSource);
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }
}