package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.ImagesCommunity;
import com.webapp.springBoot.exception.FileIsNull;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.ImagesCommunityRepository;
import com.webapp.springBoot.validation.File.UploadFileValidation;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ImageCommunityService {
    @Autowired
    private ImagesCommunityRepository imagesCommunityRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UploadFileValidation uploadFileValidation;


    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->

    public byte[] getImagePath(String nameImage) throws IOException {
        Optional<ImagesCommunity> imagesCommunityOptional = imagesCommunityRepository.findByNameImage(nameImage);
        if(imagesCommunityOptional.isEmpty()){
            throw new NoSuchElementException("Изображение не найдено");
        }
        return Files.readAllBytes(Path.of(imagesCommunityOptional.get().getImageUrl()));
    }

    private Community getCommunity(String nickname){
        Optional<Community> optionalCommunity = communityRepository.findByNickname(nickname);
        if(optionalCommunity.isEmpty()){
            throw new NoSuchElementException("Nickname сообщества не найден");
        }
        return optionalCommunity.get();
    }

    // <----------------УДАЛЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    @Transactional
    public void deleteImageCommunity(String nickname) throws IOException {
        Community community = getCommunity(nickname);
        ImagesCommunity imagesCommunity = community.getImageUrlId();
        if(imagesCommunity == null){
            throw new NoSuchElementException("Изображение сообщества не найдено");
        }
        String path = imagesCommunity.getImageUrl();
        community.setImageUrlId(null);
        communityRepository.save(community);
        imagesCommunityRepository.delete(imagesCommunity);
        Files.delete(Path.of(path));
    }
    // <----------------СОЗДАНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    public void createImagesCommunty(String nickanme, MultipartFile file) throws IOException, ValidationErrorWithMethod {
        Community community = getCommunity(nickanme);
        ImagesCommunity imagesCommunity = new ImagesCommunity();
        String path = uploadFileValidation.validationFileAndUpload(file, "image/png", imagesCommunity.getNameImage());
        imagesCommunity.setImageUrl(path);
        community.setImageUrlId(imagesCommunity);
        communityRepository.save(community);
    }
    // <----------------ИЗМЕНЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    public void setImagesCommunity(String nickname) throws IOException {

    }
}
