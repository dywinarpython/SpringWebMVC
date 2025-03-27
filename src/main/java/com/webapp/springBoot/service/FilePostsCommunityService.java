package com.webapp.springBoot.service;

import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsCommunityFile;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.*;
import com.webapp.springBoot.validation.File.UploadFileValidationImage;
import com.webapp.springBoot.validation.File.UploadFileValidationVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class FilePostsCommunityService {

    @Autowired
    private FilePostsCommunityRepository filePostsCommunityRepository;

    @Autowired
    private PostsCommunityRepository postsCommunityRepository;

    @Autowired
    private UploadFileValidationVideo uploadFileValidationVideo;

    @Autowired
    private UploadFileValidationImage uploadFileValidationImage;

    @Autowired
    private CommunityRepository communityRepository;

    @Value("${type.Image}")
    private String typeImage;

    @Value("${type.VIDEO}")
    private String typeVideo;

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsCommunityService ----------------------------->

    public String getFile(String nameFile) {
        Optional<PostsCommunityFile> fileOptional = filePostsCommunityRepository.findByNameFile(nameFile);
        if (fileOptional.isEmpty()) {
            throw new NoSuchElementException("Файл не найден");
        }
        return fileOptional.get().getFileUrl();
    }

    public List<String> getFileName(PostsCommunity postsCommunity) {
        List<PostsCommunityFile> postsCommunityFileList = postsCommunity.getFiles();
        if (postsCommunityFileList == null) {
            return null;
        }
        List<String> stringList = new ArrayList<>();
        postsCommunityFileList.forEach(
                tapeUserAppForeach -> stringList.add(tapeUserAppForeach.getNameFile()));
        return stringList;
    }

    // <----------------УДАЛЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsCommunityService ----------------------------->
    public void deleteFilePostsCommunityService(PostsCommunity postsCommunity) throws IOException {
        List<PostsCommunityFile> postsCommunityFileList = postsCommunity.getFiles();
        if (!(postsCommunityFileList == null)) {
            for (PostsCommunityFile postsCommunityFile : postsCommunityFileList) {
                String path = postsCommunityFile.getFileUrl();
                Files.delete(Path.of(path));
                filePostsCommunityRepository.delete(postsCommunityFile);
            }
        }
        postsCommunity.setFiles(null);
    }

    // <----------------СОЗДАНИЕ ДАННЫХ В СУЩНОСТИ FilePostsCommunityService---------------------------->
    public void createFIlesForPosts(MultipartFile[] multipartFiles, PostsCommunity postsCommunity)
            throws IOException, ValidationErrorWithMethod {
        List<PostsCommunityFile> postsCommunityFileList  = new ArrayList<>();
        String path;
        for (MultipartFile file : multipartFiles) {
            PostsCommunityFile postsCommunityFile = new PostsCommunityFile();
            if (Objects.equals(file.getContentType(), "image/" + typeImage)) {
                path = uploadFileValidationImage.uploadFile(file, postsCommunityFile.getNameFile(),
                        UploadFileValidationImage.UploadTypeEntity.COMMUNITY_POSTS, typeImage);
            } else if (Objects.equals(file.getContentType(), "video/" + typeVideo)) {
                path = uploadFileValidationVideo.uploadFile(file, postsCommunityFile.getNameFile(),
                        UploadFileValidationVideo.UploadTypeEntity.COMMUNITY_POSTS, typeVideo);
            } else {
                throw new ValidationErrorWithMethod(STR."Какой то из файлов некоректного типа, а именно: \{file.getContentType()} доступные типы: \{typeImage} \{typeVideo}");
            }
            postsCommunityFile.setImageUrl(path);
            postsCommunityFileList.add(postsCommunityFile);
        }
        postsCommunity.setFiles(postsCommunityFileList);
    }

    // <----------------ИЗМЕНЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsCommunityService---------------------------->
    public void setFileTapeUsersAppService(MultipartFile[] file, PostsCommunity postsCommunity)
            throws IOException, ValidationErrorWithMethod {
        if (file.length > 7) {
            throw new ValidationErrorWithMethod(
                    "Количество файлов для загрузки не может превышать 7, то есть на пост максимум 7 файлов");
        }
        deleteFilePostsCommunityService(postsCommunity);
        createFIlesForPosts(file, postsCommunity);
    }
}
