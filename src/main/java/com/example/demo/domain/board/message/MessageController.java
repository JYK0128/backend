package com.example.demo.domain.board.message;


import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.domain.board.upload.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class MessageController extends RepositoryRestExceptionHandler {
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;

    @Autowired
    MessageController(MessageSource messageSource,
                      PostRepository postRepository,
                      UploadRepository uploadRepository,
                      MessageRepository messageRepository) {
        super(messageSource);
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/message")
    public Object createMessage(PersistentEntityResourceAssembler assembler,
                                @RequestBody EntityModel<Message> entityModel) {
        Message message = entityModel.getContent();
        Message topic = message.getTopic();
        message.setTopic(topic);

        messageRepository.save(message);
        return new ResponseEntity(assembler.toFullResource(message), HttpStatus.CREATED);
    }
}