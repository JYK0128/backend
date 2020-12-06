package com.example.demo.util;

import com.example.demo.domain.board.upload.Upload;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final String projDir = System.getProperty("user.dir");
    private static final String fileDir = projDir + "/src/main/webapp/FILE-SYS/";

    public static Upload upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new NullPointerException("file is null");

        Upload upload = Upload.builder().filename(file.getOriginalFilename()).build();
        file.transferTo(new File(fileDir, upload.getUuid()));

        return upload;
    }

    public static void delete(Upload upload) {
        File file = new File(fileDir, upload.getUuid());
        file.delete();
    }
}