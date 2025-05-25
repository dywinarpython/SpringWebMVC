package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
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

    @Qualifier("stringKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserPostReactionService userPostReactionService;

    // <------------------------ ПРОВЕРКА В СУЩНОСТИ PostUsersAppService-------------------------->
    public void checkPostUserByNicknameUser(UsersApp usersApp, String nicknameUser){
        if(!usersApp.equals(usersService.findUsersByNickname(nicknameUser))){
            throw new LockedException("Пользователь не имеет право управлять данным постом");
        }
    }
    public PostsUserApp checkPostUserByNicknameUser(String namePost, String nicknameUser){
        Optional<PostsUserApp> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsUserApp postsUserApp = optionalPostsUserApp.get();
        if(postsUserApp.getUsersApp().equals(usersService.findUsersByNickname(nicknameUser))){
            return postsUserApp;
        } else {
            throw new LockedException("Пользователь не имеет право управлять данным постом");
        }
    }

    // <------------------------ ПОЛУЧЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Cacheable(value = "POST_LIST", key = "#nickname")
    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsForMe(String nickname){
        UsersApp usersApp = usersService.findUsersByNickname(nickname);
        List<PostsUserApp> postsUserAppList = usersApp.getPostsUserAppList();
        List<ResponsePostDTO> usersPostDTOList = new ArrayList<>();
        postsUserAppList.forEach(postsUserApp -> {
            boolean set;
            LocalDateTime localDateTime;
            if(postsUserApp.getUpdateDate() != null){
                set = true;
                localDateTime = postsUserApp.getUpdateDate();
            } else {
                set = false;
                localDateTime = postsUserApp.getCreateDate();
            }
            usersPostDTOList.add(new ResponsePostDTO(
                            postsUserApp.getTitle(),
                            postsUserApp.getDescription(),
                            nickname,
                            postsUserApp.getName(),
                            filePostsUsersAppService.getFileName(postsUserApp),
                            localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                            set,
                            false,
                            postsUserApp.getRating()
                    ));
                }
                );
        return new ResponseListPostDTO(usersPostDTOList);
    }

    // С кешом пока что не работаем по скольоку будет разделения с пагинацией
    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsForNickname(String nickname, String nicknameGet, int page) throws ValidationErrorWithMethod {
        if(nickname.equals(nicknameGet)){
            throw new ValidationErrorWithMethod("Пользователь не может получить свои же собственные данные, в качестве друга");
        }
        List<PostsUserApp> postsUserAppList = postsUsersAppRepository.findByUserId(usersService.getIdWithNickname(nickname), PageRequest.of(page, 5));
        List<ResponsePostDTO> usersPostDTOList = new ArrayList<>();
        Cache cache = cacheManager.getCache("REACTION");
        if(cache == null){
            log.error("Не возможно получить пост пользователя, кеш не доступен");
            throw new RuntimeException("Кеш не доступен");
        }

        postsUserAppList.forEach(postsUserApp -> {
                    boolean set;
                    LocalDateTime localDateTime;
                    if(postsUserApp.getUpdateDate() != null){
                        set = true;
                        localDateTime = postsUserApp.getUpdateDate();
                    } else {
                        set = false;
                        localDateTime = postsUserApp.getCreateDate();
                    }
                    usersPostDTOList.add(new ResponsePostDTO(
                            postsUserApp.getTitle(),
                            postsUserApp.getDescription(),
                            nickname,
                            postsUserApp.getName(),
                            filePostsUsersAppService.getFileName(postsUserApp),
                            localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                            set,
                            false,
                            postsUserApp.getRating()
                    ));
                }
        );
        return new ResponseListPostDTO(usersPostDTOList);
    }

    // как то продумать кеш...
    public ResponseListPostDTOReaction getPostsByNicknameReaction(String nickname, int page, String nicknameGet) throws ValidationErrorWithMethod {
        ResponseListPostDTO responseListPostDTO = getPostsForNickname(nickname, nicknameGet, page);
        List<ResponsePostDTOReaction> responsePostDTOReactions = new ArrayList<>();
        responseListPostDTO.getPosts().forEach( userPostDTOList -> {
            ResponsePostDTOReaction responsePostDTOReaction = new ResponsePostDTOReaction();
            responsePostDTOReaction.setResponsePostDTO(userPostDTOList);
            responsePostDTOReaction.setReaction(userPostReactionService.getRating(nicknameGet, userPostDTOList.getNamePost()));
            responsePostDTOReactions.add(responsePostDTOReaction);
        });
        return new ResponseListPostDTOReaction(responsePostDTOReactions);
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

    public ResponsePostDTO getPost(String namePost){
        Optional<PostsUserApp> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsUserApp postsUserApp = optionalPostsUserApp.get();
        boolean set;
        LocalDateTime localDateTime;
        if(postsUserApp.getUpdateDate() != null){
            set = true;
            localDateTime = postsUserApp.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsUserApp.getCreateDate();
        }
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш для записи поста не доступен");
            throw new RuntimeException("Кеш не дотсупен");
        }
        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set, false, postsUserApp.getRating());
        cache.put(namePost, responsePostDTO);
        return responsePostDTO;
    }

    public ResponsePostDTOReaction getPostWithReaction(String namePost, String nicknameUser){
        Cache cachePost = cacheManager.getCache("POST");
        Cache cache = cacheManager.getCache("REACTION");
        if(cache == null || cachePost == null){
            log.error("Не возможно получить пост пользователя, кеш не доступен");
            throw new RuntimeException("Кеш не доступен");
        }
        ResponsePostDTO responsePostDTO = cachePost.get(namePost, ResponsePostDTO.class);
        if(responsePostDTO == null){
            responsePostDTO = getPost(namePost);
            cachePost.put(namePost, responsePostDTO);
        }
        Integer reaction = cache.get(nicknameUser + ':' + responsePostDTO.getNamePost(), Integer.class);
        if(reaction == null){
            reaction = userPostReactionService.getRating(nicknameUser, responsePostDTO.getNamePost());
        }
        return new ResponsePostDTOReaction(responsePostDTO, reaction);
    }


    public ResponsePostDTO getPostNull(String namePost){
        Optional<PostsUserApp> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            return null;
        }
        PostsUserApp postsUserApp = optionalPostsUserApp.get();
        boolean set;
        LocalDateTime localDateTime;
        if(postsUserApp.getUpdateDate() != null){
            set = true;
            localDateTime = postsUserApp.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsUserApp.getCreateDate();
        }
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
           log.error("Кеш не доступен, пост не может быть положен в кеш");
           throw new RuntimeException("Кеш не доступен");
        }

        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set, false, postsUserApp.getRating());
        cache.put(namePost, responsePostDTO);
        return responsePostDTO;

    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostUsersAppService-------------------------->
    public Optional<PostsUserApp>  findByName(String name) {
        return postsUsersAppRepository.findByName(name);
    }



    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Caching(evict = {
            @CacheEvict(value = "POST", key = "#namePost"),
            @CacheEvict(value = "POST_LIST", key = "#nicknameUser")
    })
    @Transactional
    public void deletePostUsersApp(String namePost, String nicknameUser) throws IOException {
        Optional<PostsUserApp> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsUserApp postsUserApp = optionalPostsUserApp.get();
        checkPostUserByNicknameUser(postsUserApp.getUsersApp(), nicknameUser);
        postsUserApp.setUsersApp(null);
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        postsUsersAppRepository.delete(postsUserApp);
        kafkaTemplate.send("news-feed-topic-namePost-del", null, postsUserApp.getName());
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @CacheEvict(value = "POST_LIST", key = "#nicknameUser")
    @Transactional
    public ResponsePostDTO createPostUsersApp(RequestPostDTO requestPostDTO, String nicknameUser, BindingResult bindingResult,
                                              MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        boolean flag = false;
        PostsUserApp postsUserApp = new PostsUserApp();
        postsUserApp.setUsersApp(usersService.findUsersByNickname(nicknameUser));
        if(requestPostDTO.getTitle() != null){
            postsUserApp.setTitle(requestPostDTO.getTitle());
            flag = true;
        }
        if(requestPostDTO.getDescription() != null){
            postsUserApp.setDescription(requestPostDTO.getDescription());
            flag = true;
        }
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Не переданы необходимые параметры для создания поста пользователя");
        }
        postsUserApp.generateName();
        postsUserApp.setRating(0L);
        postsUsersAppRepository.save(postsUserApp);
        kafkaTemplate.send("news-feed-topic-user", nicknameUser, postsUserApp.getName());
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), LocalDateTime.now(), false, false, 0L);
    }
    // <------------------------ ИЗМЕНЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @CacheEvict(value = "POST_LIST", key = "#nicknameUser")
    @Transactional
    public ResponsePostDTO setPostUserApp(SetPostDTO setPostDTO, String nicknameUser, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsUserApp postsUserApp = checkPostUserByNicknameUser(setPostDTO.getNamePost(), nicknameUser);
        if(setPostDTO.getTitle() != null) {
            postsUserApp.setTitle(setPostDTO.getTitle());
        }
        postsUserApp.setDescription(setPostDTO.getDescription());
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
        }
        postsUserApp.setUpdateDate(LocalDateTime.now());
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), postsUserApp.getUpdateDate(), true, false, postsUserApp.getRating());
    }
}
