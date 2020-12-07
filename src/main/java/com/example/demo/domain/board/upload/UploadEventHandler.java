package com.example.demo.domain.board.upload;

import com.example.demo.util.FileUtils;

import javax.persistence.PreRemove;

public class UploadEventHandler {

    @PreRemove
    public void preRemove(Upload upload){
        FileUtils.delete(upload);
    }
}
