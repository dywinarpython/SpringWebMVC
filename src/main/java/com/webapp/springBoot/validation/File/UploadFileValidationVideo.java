package com.webapp.springBoot.validation.File;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadFileValidationVideo {
    @Value("${upload.pathCommunity.video}")
    private  String uploadPathCommunity;

    @Value("${upload.pathUser.video}")
    private  String uploadPathUser;


    public enum UploadTypeEntity{
        COMMUNITY_POSTS, USER_POSTS
    }

    public String uploadFile(MultipartFile file, String nameFile, UploadTypeEntity uploadTypeEntity, String typeVideo) throws IOException {
        String uploadPath;
        switch (uploadTypeEntity){
            case COMMUNITY_POSTS -> uploadPath = uploadPathCommunity;
            case USER_POSTS -> uploadPath = uploadPathUser;
            default -> uploadPath = "uploads/";
        }
        Path path = Paths.get(uploadPath, nameFile + "." + typeVideo);
        Files.write(path, file.getBytes());
        return path.toString();
    }

}