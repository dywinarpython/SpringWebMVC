package com.webapp.springBoot.service;

import com.webapp.springBoot.entity.ImagesCommunity;
import com.webapp.springBoot.repository.ImagesCommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImagesCommunityService {
    @Autowired
    private ImagesCommunityRepository imagesRepository;

    @Value("${upload.path}")
    private String uploadPath;



    public ImagesCommunity newImages(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Файл не найден");
        }
        UUID uuidImage =  UUID.randomUUID();
        Path path = Paths.get(uploadPath, uuidImage.toString());
        FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(path));
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();

        ImagesCommunity images = new ImagesCommunity(uuidImage, path.toString());
        imagesRepository.save(images);
        return images;
    }
}
