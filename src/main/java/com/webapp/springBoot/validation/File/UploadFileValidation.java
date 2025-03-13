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
import java.util.UUID;

@Component
public class UploadFileValidation {
    @Value("${upload.path}")
    private  String uploadPath;

    @Value("${type.Image}")
    private String typeImage;

    public String validationFileAndUpload(MultipartFile file, String typeFile) throws ValidationErrorWithMethod, IOException {
        if(!Objects.equals(file.getContentType(), typeFile)){
            throw new ValidationErrorWithMethod("Файл не соответсвует ождиаемому типу, а именно:" + typeFile);
        }
        return uploadFile(file);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString();
        Path path = Paths.get(uploadPath, fileName+ typeImage);

        Files.write(path, file.getBytes());
        return path.toString();
    }

}
