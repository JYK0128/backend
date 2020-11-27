package com.example.demo.controller;


import com.example.demo.domain.board.Upload;
import com.example.demo.repository.board.UploadRepository;
import com.example.demo.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RepositoryRestController
public class RepoController {
    private final UploadRepository uploadRepository;

    @Autowired
    RepoController(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    @PostMapping("/upload")
    private Object upload(@RequestPart MultipartFile file) {
        try {
            Upload upload = FileUtils.upload(file);
            uploadRepository.save(upload);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
