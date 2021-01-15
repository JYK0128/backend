package com.example.demo.util;

import com.example.demo.domain.board.upload.Upload;
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

    public static File getFile(String uuid) {
        return new File(fileDir + uuid);
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