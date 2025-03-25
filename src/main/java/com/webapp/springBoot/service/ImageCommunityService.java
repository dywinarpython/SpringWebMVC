package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.ImagesCommunity;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.ImageCommunityRepository;
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
public class ImageCommunityService {
    @Autowired
    private ImageCommunityRepository imagesCommunityRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UploadFileValidationImage uploadFileValidation;
    @Value("${type.Image}")
    private String typeImage;


    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->

    public byte[] getImagePath(String nameImage) throws IOException {
        Optional<ImagesCommunity> imagesCommunityOptional = imagesCommunityRepository.findByNameImage(nameImage);
        if(imagesCommunityOptional.isEmpty()){
            throw new NoSuchElementException("Изображение не найдено");
        }
        return Files.readAllBytes(Path.of(imagesCommunityOptional.get().getImageUrl()));
    }

    public String getImageName(Community community) {
        ImagesCommunity imagesCommunityOptional = community.getImageUrlId();
        if(imagesCommunityOptional == null){
            return null;
        }
        return imagesCommunityOptional.getNameImage();
    }



    // <----------------УДАЛЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    public void deleteImageCommunity(Community community) throws IOException {
        ImagesCommunity imagesCommunity = community.getImageUrlId();
        if(imagesCommunity != null){
            String path = imagesCommunity.getImageUrl();
            community.setImageUrl(null);
            communityRepository.save(community);
            imagesCommunityRepository.delete(imagesCommunity);
            Files.delete(Path.of(path));
        }

    }
    // <----------------СОЗДАНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    public void createImagesCommunty(MultipartFile file, Community community) throws IOException, ValidationErrorWithMethod {
        ImagesCommunity imagesCommunity = new ImagesCommunity();
        if(!Objects.equals(file.getContentType(), "image/" + typeImage)){
            throw new ValidationErrorWithMethod("Файл не соответсвует ождиаемому типу, а именно: " + typeImage);
        }
        String path = uploadFileValidation.uploadFile(file, imagesCommunity.getNameImage(), UploadFileValidationImage.UploadTypeEntity.COMMUNITY, typeImage);
        imagesCommunity.setImageUrl(path);
        community.setImageUrl(imagesCommunity);
        communityRepository.save(community);
    }
    // <----------------ИЗМЕНЕНИЕ ДАННЫХ В СУЩНОСТИ  ImageCommunity ----------------------------->
    public void setImagesCommunity(MultipartFile file, Community community) throws IOException, ValidationErrorWithMethod {
        if(community.getImageUrlId() == null){
            createImagesCommunty(file, community);
        } else {
            deleteImageCommunity(community);
            createImagesCommunty(file, community);
        }
    }
}
