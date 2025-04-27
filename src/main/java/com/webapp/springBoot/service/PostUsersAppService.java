package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.UsersPost.RequestUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponseListUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponseUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.SetUsersPostDTO;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
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

    // <------------------------ ПРОВЕРКА В СУЩНОСТИ PostUsersAppService-------------------------->
    public void checkPostUserByNicknameUser(UsersApp usersApp, String nicknameUser){
        if(!usersApp.equals(usersService.findUsersByNickname(nicknameUser))){
            throw new LockedException("Пользователь не имеет право управлять данным постом");
        }
    }
    public PostsUserApp checkPostUserByNicknameUser(String postName, String nicknameUser){
        PostsUserApp postsUserApp = findByName(postName);
        if(postsUserApp.getUsersApp().equals(usersService.findUsersByNickname(nicknameUser))){
            return postsUserApp;
        } else {
            throw new LockedException("Пользователь не имеет право управлять данным постом");
        }
    }

    // <------------------------ ПОЛУЧЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Cacheable(value = "USER_POST_LIST", key = "#nickname")
    @Transactional(readOnly = true)
    public ResponseListUsersPostDTO getPostsByNickname(String nickname){
        UsersApp usersApp = usersService.findUsersByNickname(nickname);
        List<PostsUserApp> postsUserAppList = usersApp.getPostsUserAppList();
        List<ResponseUsersPostDTO> usersPostDTOList = new ArrayList<>();
        postsUserAppList.forEach(postsUserApp ->
                usersPostDTOList.add(new ResponseUsersPostDTO(
                        postsUserApp.getTitle(),
                        postsUserApp.getDescription(),
                        nickname,
                        postsUserApp.getName(),
                        filePostsUsersAppService.getFileName(postsUserApp)
                ))
                );
        return new ResponseListUsersPostDTO(usersPostDTOList);
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
     @Cacheable(value = "USER_POST", key = "#namePost")
     public ResponseUsersPostDTO getPost(String namePost){
        PostsUserApp postsUserApp = findByName(namePost);
        return new ResponseUsersPostDTO(postsUserApp, postsUserApp.getUsersApp().getNickname(), filePostsUsersAppService.getFileName(postsUserApp));
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
    @Caching(evict = {
            @CacheEvict(value = "USER_POST", key = "#namePost"),
            @CacheEvict(value = "USER_POST_LIST", key = "#nicknameUser")
    })
    @Transactional
    public void deletePostUsersApp(String namePost, String nicknameUser) throws IOException {
        PostsUserApp postsUserApp = findByName(namePost);
        checkPostUserByNicknameUser(postsUserApp.getUsersApp(), nicknameUser);
        postsUserApp.setUsersApp(null);
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        postsUsersAppRepository.delete(postsUserApp);
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @CacheEvict(value = "USER_POST_LIST", key = "#nicknameUser")
    @CachePut(value = "USER_POST" , key = "#result.getNamePost()")
    @Transactional
    public ResponseUsersPostDTO createPostUsersApp(RequestUsersPostDTO requestUsersPostDTO, String nicknameUser, BindingResult bindingResult,
                                                   MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        boolean flag = false;
        PostsUserApp postsUserApp = new PostsUserApp();
        postsUserApp.setUsersApp(usersService.findUsersByNickname(nicknameUser));
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
        postsUserApp.setTitle(requestUsersPostDTO.getTitle());
        postsUserApp.generateName();
        postsUserApp.setDescription(requestUsersPostDTO.getDescription());
        return new ResponseUsersPostDTO(postsUsersAppRepository.save(postsUserApp), nicknameUser, filePostsUsersAppService.getFileName(postsUserApp));
    }
    @CacheEvict(value = "USER_POST_LIST", key = "#nicknameUser")
    @CachePut(value = "USER_POST" , key = "#result.getNamePost()")
    @Transactional
    public ResponseUsersPostDTO setPostUserApp(SetUsersPostDTO setUsersPostDTO, String nicknameUser, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsUserApp postsUserApp = checkPostUserByNicknameUser(setUsersPostDTO.getNamePost(), nicknameUser);
        if(setUsersPostDTO.getTitle() != null) {
            postsUserApp.setTitle(setUsersPostDTO.getTitle());
        }
        postsUserApp.setDescription(setUsersPostDTO.getDescription());
        List<String> fileNames;
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
            fileNames = filePostsUsersAppService.getFileName(postsUserApp);
        } else {
            fileNames = null;
        }
        postsUserApp.setUpdateDate(LocalDateTime.now());
        return new ResponseUsersPostDTO(postsUsersAppRepository.save(postsUserApp), nicknameUser, fileNames);
    }
}
