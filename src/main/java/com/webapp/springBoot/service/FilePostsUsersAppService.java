package com.webapp.springBoot.service;

import com.webapp.springBoot.entity.PostsUserAppFile;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.FilePostsUsersAppRepository;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
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
public class FilePostsUsersAppService {

    @Autowired
    private FilePostsUsersAppRepository filePostsUserAppRepository;

    @Autowired
    private PostsUsersAppRepository postsUsersAppRepository;

    @Autowired
    private UploadFileValidationVideo uploadFileValidationVideo;

    @Autowired
    private UploadFileValidationImage uploadFileValidationImage;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Value("${type.Image}")
    private String typeImage;

    @Value("${type.VIDEO}")
    private String typeVideo;

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsUsersAppService ----------------------------->

    public byte[] getFile(String nameImage) throws IOException {
        Optional<PostsUserAppFile> imagesCommunityOptional = filePostsUserAppRepository.findByNameFile(nameImage);
        if (imagesCommunityOptional.isEmpty()) {
            throw new NoSuchElementException("Файл не найден");
        }
        return Files.readAllBytes(Path.of(imagesCommunityOptional.get().getFileUrl()));
    }

    public List<String> getFileName(PostsUserApp tapeUserApp) {
        List<PostsUserAppFile> tapeUserAppFileList = tapeUserApp.getFiles();
        if (tapeUserAppFileList == null) {
            return null;
        }
        List<String> stringList = new ArrayList<>();
        tapeUserAppFileList.forEach(
                tapeUserAppForeach -> stringList.add(tapeUserAppForeach.getNameFile()));
        return stringList;
    }

    // <----------------УДАЛЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsUsersAppService ----------------------------->
    public void deleteFileTapeUsersAppService(PostsUserApp postsUserApp) throws IOException {
        List<PostsUserAppFile> postsUserAppFileList = postsUserApp.getFiles();
        postsUserApp.setUsersApp(null);
        postsUsersAppRepository.delete(postsUserApp);
        if (!(postsUserAppFileList == null)) {
            for (PostsUserAppFile postsUserAppFileFor : postsUserAppFileList) {
                String path = postsUserAppFileFor.getFileUrl();
                Files.delete(Path.of(path));
            }
        }
    }

    // <----------------СОЗДАНИЕ ДАННЫХ В СУЩНОСТИ FilePostsUsersAppService---------------------------->
    public void createFIlesForPosts(MultipartFile[] multipartFiles, PostsUserApp postsUserApp)
            throws IOException, ValidationErrorWithMethod {
        List<PostsUserAppFile> postsUserAppFileList = new ArrayList<>();
        String path;
        for (MultipartFile file : multipartFiles) {
            PostsUserAppFile postsUserAppFile = new PostsUserAppFile();
            if (Objects.equals(file.getContentType(), "image/" + typeImage)) {
                path = uploadFileValidationImage.uploadFile(file, postsUserAppFile.getNameFile(),
                        UploadFileValidationImage.UploadTypeEntity.USERS_APP_POSTS, typeImage);
            } else if (Objects.equals(file.getContentType(), "video/" + typeVideo)) {
                path = uploadFileValidationVideo.uploadFile(file, postsUserAppFile.getNameFile(),
                        UploadFileValidationVideo.UploadTypeEntity.USER_POSTS, typeVideo);
            } else {
                throw new ValidationErrorWithMethod(STR."Какой то из файлов некоректного типа, а именно: \{file.getContentType()} доступные типы: \{typeImage} \{typeVideo}");
            }
            postsUserAppFile.setImageUrl(path);
            postsUserAppFileList.add(postsUserAppFile);
        }
        postsUserApp.setFiles(postsUserAppFileList);
    }

    // <----------------ИЗМЕНЕНИЕ ДАННЫХ В СУЩНОСТИ FilePostsUsersAppService---------------------------->
    public void setFileTapeUsersAppService(MultipartFile[] file, PostsUserApp postsUserApp)
            throws IOException, ValidationErrorWithMethod {
        if (file.length > 7) {
            throw new ValidationErrorWithMethod(
                    "Количсетво файлов для загрузки не может превышать 7, то есть на пост максимум 7 файлов");
        }
        deleteFileTapeUsersAppService(postsUserApp);
        createFIlesForPosts(file, postsUserApp);
    }
}
