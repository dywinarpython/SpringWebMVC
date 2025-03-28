package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.UsersPost.RequestUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceListUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.SetUsersPostDTO;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PostUsersAppService {
    @Autowired
    private FilePostsUsersAppService filePostsUsersAppService;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PostsUsersAppRepository postsUsersAppRepository;

    // <------------------------ ПОЛУЧЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Transactional
    public ResponceListUsersPostDTO getPostsByNickname(String nickname){
        UsersApp usersApp = usersService.findUsersByNickname(nickname);
        List<PostsUserApp> postsUserAppList = usersApp.getPostsUserAppList();
        List<ResponceUsersPostDTO> usersPostDTOList = new ArrayList<>();
        postsUserAppList.forEach(postsUserApp ->
                usersPostDTOList.add(new ResponceUsersPostDTO(
                        postsUserApp.getTitle(),
                        postsUserApp.getDescription(),
                        nickname,
                        postsUserApp.getName(),
                        filePostsUsersAppService.getFileName(postsUserApp)
                ))
                );
        return new ResponceListUsersPostDTO(usersPostDTOList);
    }

    public ResponseEntity<Resource> getFilePost(String nameFile) throws IOException {
        String fileStringPath = filePostsUsersAppService.getFile(nameFile);
        String contentType = Files.probeContentType(Path.of(fileStringPath));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileStringPath));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(resource);
    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostUsersAppService-------------------------->
    public PostsUserApp findByName(String name) {
        Optional<PostsUserApp> optionalPostsUserApp = postsUsersAppRepository.findByName(name);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        return optionalPostsUserApp.get();
    }

    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Transactional
    public void deletePostUsersApp(String namePost) throws IOException {
        PostsUserApp postsUserApp = findByName(namePost);
        postsUserApp.setUsersApp(null);
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        postsUsersAppRepository.delete(postsUserApp);
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Transactional
    public void createPostUsersApp(RequestUsersPostDTO requestUsersPostDTO, BindingResult result,
            MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        boolean flag = false;
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsUserApp postsUserApp = new PostsUserApp();

        if(requestUsersPostDTO.getTitle() != null){
            postsUserApp.setTitle(requestUsersPostDTO.getTitle());
            flag = true;
        }
        if(requestUsersPostDTO.getDescription() != null){
            postsUserApp.setDescription(requestUsersPostDTO.getDescription());
            flag = true;
        }
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Не переданы необходимые параметры для создания поста пользователя");
        }
        postsUserApp.setUsersApp(usersService.findUsersByNickname(requestUsersPostDTO.getNicknameUser()));
        postsUserApp.setTitle(requestUsersPostDTO.getTitle());
        postsUserApp.generateName();
        postsUserApp.setDescription(requestUsersPostDTO.getDescription());
        postsUsersAppRepository.save(postsUserApp);
    }
    @Transactional
    public void setPostUserApp(SetUsersPostDTO setUsersPostDTO, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        boolean flag = false;
        PostsUserApp postsUserApp = findByName(setUsersPostDTO.getNamePost());
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        if(setUsersPostDTO.getTitle() != null) {
            postsUserApp.setTitle(setUsersPostDTO.getTitle());
            flag = true;
        }
        if(setUsersPostDTO.getDescription() != null){
            postsUserApp.setDescription(setUsersPostDTO.getDescription());
            flag = true;
        }
        postsUserApp.setUpdateDate(LocalDateTime.now());
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
        postsUsersAppRepository.save(postsUserApp);
    }
}
