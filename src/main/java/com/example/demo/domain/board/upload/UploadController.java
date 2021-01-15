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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
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

    @PostMapping("/upload")
    private Object createUpload(PersistentEntityResourceAssembler assembler,
                                @RequestPart MultipartFile... files) throws IOException {
        List<Upload> uploads = new ArrayList<>();
        Assert.isTrue(FileUtils.isUploadable(files), "some files are not uploadable.");

        for (MultipartFile file : files) {
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
    private void readUpload(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Upload upload = uploadRepository.findById(id).get();
        String filename = upload.getFilename();
        String uuid = upload.getUuid();
        File file = FileUtils.getFile(uuid);

        InputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition",
                String.format("attachment;filename=\"%1$s\";" +
                        "filename*=\"UTF-8''%1$s\";", URLEncoder.encode(filename, "EUC-KR")));

        FileCopyUtils.copy(in, out);
        in.close();
        out.flush();
        out.close();
    }

    @DeleteMapping({"/upload/{id}"})
    public Object deletePost(@PathVariable Long id, Principal principal) {
        Upload upload = uploadRepository.findById(id).get();
        Assert.isTrue(upload.isDeletable(principal), "upload is not deletable.");

        uploadRepository.delete(upload);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/upload")
    private Object deleteUpload(@RequestBody CollectionModel<Upload> collectionModel, Principal principal) {
        Collection<Upload> uploads = collectionModel.getContent();
        uploads.forEach(upload -> Assert.isTrue(upload.isDeletable(principal), "upload is not deletable."));

        uploadRepository.deleteInBatch(uploads);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}