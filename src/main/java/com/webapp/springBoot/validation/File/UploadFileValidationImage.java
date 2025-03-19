package com.webapp.springBoot.validation.File;

import com.webapp.springBoot.exception.ValidationErrorWithMethod;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class UploadFileValidationImage {
    @Value("${upload.pathCommunity}")
    private String uploadPathCommunity;

    @Value("${upload.pathUser}")
    private String uploadPathUser;

    @Value("${upload.pathCommunity.image}")
    private String uploadPathCommunityPosts;

    @Value("${upload.pathUser.image}")
    private String uploadPathUserPosts;

    public enum UploadTypeEntity {
        COMMUNITY, USER, COMMUNITY_POSTS, USERS_APP_POSTS
    }

    public String uploadFile(MultipartFile file, String nameFile, UploadTypeEntity uploadTypeEntity, String typeImage)
            throws IOException {
        String uploadPath;
        switch (uploadTypeEntity) {
            case USER -> uploadPath = uploadPathUser;
            case COMMUNITY -> uploadPath = uploadPathCommunity;
            case COMMUNITY_POSTS -> uploadPath = uploadPathCommunityPosts;
            case USERS_APP_POSTS -> uploadPath = uploadPathUserPosts;
            default -> uploadPath = "uploads/";
        }
        Path path = Paths.get(uploadPath, nameFile + "." + typeImage);
        Files.write(path, file.getBytes());
        return path.toString();
    }

}
