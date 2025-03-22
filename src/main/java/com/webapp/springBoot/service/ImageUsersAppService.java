package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.ImagesUsersApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.ImageUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.validation.File.UploadFileValidationImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageUsersAppService {
    @Autowired
    private ImageUsersAppRepository imageUsersAppRepository;
    @Autowired
    private UsersAppRepository userRepository;
    @Autowired
    private UploadFileValidationImage uploadFileValidation;
    @Value("${type.Image}")
    private String typeImage;

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->

    public byte[] getImagePath(String nameImage) throws IOException {
        Optional<ImagesUsersApp> imagesCommunityOptional = imageUsersAppRepository.findByNameImage(nameImage);
        if(imagesCommunityOptional.isEmpty()){
            throw new NoSuchElementException("Изображение не найдено");
        }
        return Files.readAllBytes(Path.of(imagesCommunityOptional.get().getImageUrl()));
    }

    public String getImageName(UsersApp usersApp) {
        ImagesUsersApp imagesUsersAppOptional = usersApp.getImageUrl();
        if(imagesUsersAppOptional == null){
            return null;
        }
        return imagesUsersAppOptional.getNameImage();
    }



    // <----------------УДАЛЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageUsersApp ----------------------------->
    public void deleteImageUsersApp(UsersApp usersApp) throws IOException {
        ImagesUsersApp imagesUsersApp = usersApp.getImageUrl();
        if(imagesUsersApp == null){
            throw new NoSuchElementException("Изображение сообщества не найдено");
        }
        String path = imagesUsersApp.getImageUrl();
        usersApp.setImageUrl(null);
        userRepository.save(usersApp);
        imageUsersAppRepository.delete(imagesUsersApp);
        Files.delete(Path.of(path));
    }
    // <----------------СОЗДАНИЕ ДАННЫХ В СУЩНОСТИ  ImageUsersApp ----------------------------->
    public void createImagesCommunty(MultipartFile file, UsersApp usersApp) throws IOException, ValidationErrorWithMethod {
        ImagesUsersApp imagesUsersApp = new ImagesUsersApp();
        if(!Objects.equals(file.getContentType(), "image/" + typeImage)){
            throw new ValidationErrorWithMethod("Файл не соответсвует ождиаемому типу, а именно: " + typeImage);
        }
        String path = uploadFileValidation.uploadFile(file, imagesUsersApp.getNameImage(), UploadFileValidationImage.UploadTypeEntity.USER, typeImage);
        imagesUsersApp.setImageUrl(path);
        usersApp.setImageUrl(imagesUsersApp);
        userRepository.save(usersApp);
    }
    // <----------------ИЗМЕНЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageUsersApp ----------------------------->
    public void setImagesUsersApp(MultipartFile file, UsersApp usersApp) throws IOException, ValidationErrorWithMethod {
        if(usersApp.getImageUrl() == null){
            createImagesCommunty(file, usersApp);
        } else {
            deleteImageUsersApp(usersApp);
            createImagesCommunty(file, usersApp);
        }
    }
}
