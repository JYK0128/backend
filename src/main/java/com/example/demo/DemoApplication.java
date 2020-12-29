package com.example.demo;

import com.example.demo.util.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        FileUtils.checkDir();
        SpringApplication.run(DemoApplication.class, args);
    }
}