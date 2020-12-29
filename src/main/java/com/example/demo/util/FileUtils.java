package com.example.demo.util;

import com.example.demo.domain.board.upload.Upload;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final String projDir = System.getProperty("user.dir");
    private static final String fileDir = projDir + "/src/main/webapp/FILE-SYS/";

    public static Upload upload(MultipartFile file) throws IOException {
        Upload upload = Upload.builder().filename(file.getOriginalFilename()).build();
        file.transferTo(new File(fileDir, upload.getUuid()));

        return upload;
    }

    public static ResponseEntity download(Upload upload) throws IOException{
        String fileName = upload.getFilename();
        String uuid = upload.getUuid();
        File file = new File(fileDir + uuid);
        FileSystemResource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + fileName);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity(resource, headers, HttpStatus.OK);
    }

    public static void checkDir(){
        File dir = new File(fileDir);
        if(!dir.isDirectory()) dir.mkdirs();
    }

    public static void delete(Upload upload) {
        File file = new File(fileDir, upload.getUuid());
        file.delete();
    }

    public static boolean isUploadable(MultipartFile[] files) {
        for (MultipartFile file : files) {
            Assert.isTrue(file.getSize() > 0L, "file " + "\"" + file.getOriginalFilename() + "\"" + " are empty");
        }
        return true;
    }
}