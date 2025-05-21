package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Kafka.RequestFeedDTO;
import com.webapp.springBoot.DTO.Post.RequestPostDTO;
import com.webapp.springBoot.DTO.Post.ResponseListPostDTO;
import com.webapp.springBoot.DTO.Post.ResponsePostDTO;
import com.webapp.springBoot.DTO.Post.SetPostDTO;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
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
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    public ResponseListPostDTO getPostsByNickname(String nickname){
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
                            set
                    ));
                }
                );
        return new ResponseListPostDTO(usersPostDTOList);
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
     @Cacheable(value = "POST", key = "#namePost")
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
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set);
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
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), localDateTime, set);
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
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @CacheEvict(value = "POST_LIST", key = "#nicknameUser")
    @CachePut(value = "POST" , key = "#result.getNamePost()")
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
        postsUsersAppRepository.save(postsUserApp);
        CompletableFuture<SendResult<String, String>> future =  kafkaTemplate.send("news-feed-topic-user", nicknameUser, postsUserApp.getName());
        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Возникла ошибка добавления поста в ленту", exception);
            } else {
                RecordMetadata meta = result.getRecordMetadata();
                log.info("Сообщение отправлено в топик '{}' партиция {} офсэт {}",
                        meta.topic(), meta.partition(), meta.offset());
            }
        });
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), LocalDateTime.now(), false);

    }
    @CacheEvict(value = "POST_LIST", key = "#nicknameUser")
    @CachePut(value = "POST" , key = "#result.getNamePost()")
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
        List<String> fileNames;
        filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        if(multipartFiles!=null) {
            filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
            fileNames = filePostsUsersAppService.getFileName(postsUserApp);
        } else {
            fileNames = null;
        }
        postsUserApp.setUpdateDate(LocalDateTime.now());
        return new ResponsePostDTO(postsUserApp.getTitle(), postsUserApp.getDescription(),postsUserApp.getName(), postsUserApp.getUsersApp().getNickname() , filePostsUsersAppService.getFileName(postsUserApp), postsUserApp.getUpdateDate(), true);

    }
}
