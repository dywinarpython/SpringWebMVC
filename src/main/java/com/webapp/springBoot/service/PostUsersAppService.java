package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.DTO.UserReaction.ListUserReactionDTO;
import com.webapp.springBoot.DTO.UserReaction.UserReactionDTO;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UserPostReaction;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsForUser(String nickname, Integer page){
        List<PostsUserApp> postsUserAppList = postsUsersAppRepository.findByUserNickname(nickname, PageRequest.of(page, 5));
        return getPostWithPostList(postsUserAppList);
    }

    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsForFriend(String nickname, String nicknameGet, int page) throws ValidationErrorWithMethod {
        if(nickname.equals(nicknameGet)){
            throw new ValidationErrorWithMethod("Пользователь не может получить свои же собственные данные, в качестве друга");
        }
        return getPostsForUser(nickname, page);
    }

    @Transactional(readOnly = true)
    public ResponseListPostDTOReaction getPostsByNicknameReaction(String nickname, int page, String nicknameGet) throws ValidationErrorWithMethod {
        ResponseListPostDTO responseListPostDTO = getPostsForFriend(nickname, nicknameGet, page);
        ListUserReactionDTO userPostReactionsDto = userPostReactionService.getRating(nicknameGet,
                responseListPostDTO.getPosts().stream()
                        .map(ResponsePostDTO::getNamePost
                        ).toList()
        );
        return getPostWithReaction(userPostReactionsDto, responseListPostDTO);
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

    public ResponseListPostDTOReaction getPostWithReaction(ListUserReactionDTO  userReactionDTO, ResponseListPostDTO responseListPostDTO){
        Map<String, Integer> integerMap = userReactionDTO.getUserReactionDTO()
                .stream().collect(Collectors.toMap(
                        UserReactionDTO::getNamePost, UserReactionDTO::getReaction));
        List<ResponsePostDTOReaction> responsePostDTOReactions = new ArrayList<>();
        responseListPostDTO.getPosts().forEach(post -> {
            ResponsePostDTOReaction responsePostDTOReaction = new ResponsePostDTOReaction();
            responsePostDTOReaction.setResponsePostDTO(post);
            responsePostDTOReaction.setReaction(integerMap.get(post.getNamePost()) == null ? 0: integerMap.get(post.getNamePost()));
            responsePostDTOReactions.add(responsePostDTOReaction);
        });
        return new ResponseListPostDTOReaction(responsePostDTOReactions);
    }

    private ResponsePostDTO getPost(PostsUserApp postsUserApp){
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш для записи поста не доступен");
            throw new RuntimeException("Кеш не дотсупен");
        }
        ResponsePostDTO responsePostDTO = cache.get(postsUserApp.getName(), ResponsePostDTO.class);
        if(responsePostDTO != null){
            return responsePostDTO;
        }
        return createPostDTO(postsUserApp, cache);
    }

    public ResponseListPostDTO getPostWithPostList(List<PostsUserApp> postsUserAppList){
        List<ResponsePostDTO> responsePostDTOList = new ArrayList<>();
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш для записи поста не доступен");
            throw new RuntimeException("Кеш не дотсупен");
        }
        postsUserAppList.forEach(postsUserApp -> responsePostDTOList.add(getPost(postsUserApp)));
        return new ResponseListPostDTO(responsePostDTOList);
    }

    public ResponsePostDTO getPostWithName(String namePost){
        Optional<PostsUserApp> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsUserApp postsUserApp = optionalPostsUserApp.get();
        return getPost(postsUserApp);
    }

    @Transactional(readOnly = true)
    public ResponsePostDTOReaction getPostWithReaction(String namePost, String nicknameUser){
        Cache cachePost = cacheManager.getCache("POST");
        Cache cache = cacheManager.getCache("REACTION");
        if(cache == null || cachePost == null){
            log.error("Не возможно получить пост пользователя, кеш не доступен");
            throw new RuntimeException("Кеш не доступен");
        }

        ResponsePostDTO responsePostDTO = cachePost.get(namePost, ResponsePostDTO.class);
        if(responsePostDTO == null){
            responsePostDTO = getPostWithName(namePost);
        }
        if(responsePostDTO.getNickname().equals(nicknameUser)){
            return new ResponsePostDTOReaction(responsePostDTO);
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
        return getPost(postsUserApp);
    }

    public List<PostsUserApp> getPostUsersApp(List<String> namePosts){
        return postsUsersAppRepository.findPostByNamePosts(namePosts);
    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostUsersAppService-------------------------->
    public Optional<PostsUserApp>  findByName(String name) {
        return postsUsersAppRepository.findByName(name);
    }



    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @CacheEvict(value = "POST", key = "#namePost")
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
    // <------------------------ Util В СУЩНОСТИ PostUsersAppService-------------------------->
    public ResponsePostDTO createPostDTO(PostsUserApp postsUserApp, Cache cache){
        boolean set;
        LocalDateTime localDateTime;
        if(postsUserApp.getUpdateDate() != null){
            set = true;
            localDateTime = postsUserApp.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsUserApp.getCreateDate();
        }
        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set, false, postsUserApp.getRating());
        cache.put(postsUserApp.getName(), responsePostDTO);
        return responsePostDTO;
    }
    public ResponsePostDTO createPostDTO(PostsUserApp postsUserApp){
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш для записи поста не доступен");
            throw new RuntimeException("Кеш не дотсупен");
        }
        boolean set;
        LocalDateTime localDateTime;
        if(postsUserApp.getUpdateDate() != null){
            set = true;
            localDateTime = postsUserApp.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsUserApp.getCreateDate();
        }
        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set, false, postsUserApp.getRating());
        cache.put(postsUserApp.getName(), responsePostDTO);
        return responsePostDTO;
    }
}
