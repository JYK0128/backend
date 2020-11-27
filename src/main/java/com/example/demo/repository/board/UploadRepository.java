package com.example.demo.repository.board;

import com.example.demo.domain.board.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="upload")
public interface UploadRepository extends JpaRepository<Upload, Long> {

}
