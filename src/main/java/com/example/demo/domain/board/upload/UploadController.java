package com.example.demo.domain.board.upload;


import com.example.demo.domain.board.message.MessageRepository;
import com.example.demo.domain.board.post.PostRepository;
import com.example.demo.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RepositoryRestController
public class UploadController extends RepositoryRestExceptionHandler {
    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;
    private final MessageRepository messageRepository;

    @Autowired
    UploadController(MessageSource messageSource,
                     PostRepository postRepository,
                     UploadRepository uploadRepository,
                     MessageRepository messageRepository) {
        super(messageSource);
        this.postRepository = postRepository;
        this.uploadRepository = uploadRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/upload")
    private Object createUpload(PersistentEntityResourceAssembler assembler,
                                @RequestPart MultipartFile... files) throws IOException {
        List<Upload> uploads = new ArrayList<>();
        for (MultipartFile file : files) uploads.add(FileUtils.upload(file));

        uploads = uploadRepository.saveAll(uploads);
        List<PersistentEntityResource> collection = uploads.stream()
                .map(upload -> assembler.toFullResource(upload))
                .collect(Collectors.toList());

        return new ResponseEntity(CollectionModel.of(collection), HttpStatus.CREATED);
    }
}