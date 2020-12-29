package com.example.demo.domain.board.upload;


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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RepositoryRestController
public class UploadController extends RepositoryRestExceptionHandler {
    private final UploadRepository uploadRepository;

    @Autowired
    UploadController(MessageSource messageSource,
                     UploadRepository uploadRepository) {
        super(messageSource);
        this.uploadRepository = uploadRepository;
    }

    //TODO: ResponseEntity not working in Win? in Linux?
    @PostMapping("/upload")
    private Object createUpload(PersistentEntityResourceAssembler assembler,
                                @RequestPart MultipartFile... files) throws IOException {
        List<Upload> uploads = new ArrayList<>();
        Assert.isTrue(FileUtils.isUploadable(files), "some files are not uploadable.");

        for (MultipartFile file: files) {
            Upload upload = FileUtils.upload(file);
            uploads.add(upload);
        }

        uploads = uploadRepository.saveAll(uploads);
        List<PersistentEntityResource> collection = uploads.stream()
                .map(upload -> assembler.toFullResource(upload))
                .collect(Collectors.toList());
        return new ResponseEntity(CollectionModel.of(collection), HttpStatus.CREATED);
    }

    @GetMapping("/upload/{id}")
    private Object readUpload(@PathVariable Long id) throws IOException {
        Upload upload = uploadRepository.findById(id).get();
        return FileUtils.download(upload);
    }

    @DeleteMapping({"/upload/{id}"})
    public Object deletePost(@PathVariable Long id, Principal principal) {
        Upload upload = uploadRepository.findById(id).get();
        Assert.isTrue(upload.isDeletable(principal), "upload is not deletable.");

        uploadRepository.delete(upload);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}