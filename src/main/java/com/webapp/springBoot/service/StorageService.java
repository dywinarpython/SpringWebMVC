package com.webapp.springBoot.service;



import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageService {

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() throws IOException {
        Path path = Path.of(uploadPath);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
    }
}
