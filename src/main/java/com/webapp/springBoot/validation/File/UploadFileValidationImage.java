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
    @Value("${upload.pathCommunuty}")
    private  String uploadPathCommunity;

    @Value("${upload.pathUser}")
    private  String uploadPathUser;


    public enum UploadTypeEntity{
        COMMUNITY, USER
    }


    public String uploadFile(MultipartFile file, String nameFile, UploadTypeEntity uploadTypeEntity, String typeImage) throws IOException {
        String uploadPath;
        switch (uploadTypeEntity){
            case USER -> uploadPath = uploadPathUser;
            case COMMUNITY -> uploadPath = uploadPathCommunity;
            default -> uploadPath = "uploads/";
        }
        Path path = Paths.get(uploadPath, nameFile + "." + typeImage);
        Files.write(path, file.getBytes());
        return path.toString();
    }

}
