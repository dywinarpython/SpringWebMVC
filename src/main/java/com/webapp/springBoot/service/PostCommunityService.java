package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.PostsCommunityRepository;
import com.webapp.springBoot.repository.UserPostReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class PostCommunityService {
    @Autowired
    private FilePostsCommunityService filePostsCommunityService;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PostsCommunityRepository postsCommunityRepository;

    @Qualifier("stringKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserPostReactionService userPostReactionService;

    // <------------------------ ПОЛУЧЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsByNickname(String nickname, int page){
        Community community = communityService.findCommunityByNickname(nickname);
        List<PostsCommunity> postsCommunityList = postsCommunityRepository.findByCommunityId(community.getId(), PageRequest.of(page, 5));
        List<ResponsePostDTO> responseCommunityPostDTO = new ArrayList<>();
        postsCommunityList.forEach(postsCommunity -> {
                    boolean set;
                    LocalDateTime localDateTime;
                    if(postsCommunity.getUpdateDate() != null){
                        set = true;
                        localDateTime = postsCommunity.getUpdateDate();
                    } else {
                        set = false;
                        localDateTime = postsCommunity.getCreateDate();
                    }
                    responseCommunityPostDTO.add(new ResponsePostDTO(
                            postsCommunity.getTitle(),
                            postsCommunity.getDescription(),
                            nickname,
                            postsCommunity.getName(),
                            filePostsCommunityService.getFileName(postsCommunity),
                            localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                            set,
                            true,
                            postsCommunity.getRating()
                    ));
                }
        );
        return new ResponseListPostDTO(responseCommunityPostDTO);
    }

    // как то продумать кеш...
    public ResponseListPostDTOReaction getPostsByNicknameReaction(String nickname, int page, String nicknameUser){
        ResponseListPostDTO responseListPostDTO = getPostsByNickname(nickname, page);
        List<ResponsePostDTOReaction> responsePostDTOReactions = new ArrayList<>();
        responseListPostDTO.getPosts().forEach( userPostDTOList -> {
            ResponsePostDTOReaction responsePostDTOReaction = new ResponsePostDTOReaction();
            responsePostDTOReaction.setResponsePostDTO(userPostDTOList);
            responsePostDTOReaction.setReaction(userPostReactionService.getRating(nicknameUser, userPostDTOList.getNamePost()));
            responsePostDTOReactions.add(responsePostDTOReaction);
        });
        return new ResponseListPostDTOReaction(responsePostDTOReactions);
    }

    public ResponseEntity<Resource> getFilePost(String nameFile) throws IOException {
        String fileStringPath = filePostsCommunityService.getFile(nameFile);
        String contentType = Files.probeContentType(Path.of(fileStringPath));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileStringPath));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(resource);
    }
    @Cacheable(value = "POST", key = "#namePost")
    public ResponsePostDTO getPost(String namePost){
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(namePost);
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsCommunity postsCommunity = optionalPostsCommunity.get();
        boolean set;
        LocalDateTime localDateTime;
        if(postsCommunity.getUpdateDate() != null){
            set = true;
            localDateTime = postsCommunity.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsCommunity.getCreateDate();
        }
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), localDateTime, set, true, postsCommunity.getRating());
    }

    public ResponsePostDTO getPostNull(String namePost){
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(namePost);
        if (optionalPostsCommunity.isEmpty()) {
            return null;
        }
        PostsCommunity postsCommunity = optionalPostsCommunity.get();
        boolean set;
        LocalDateTime localDateTime;
        if(postsCommunity.getUpdateDate() != null){
            set = true;
            localDateTime = postsCommunity.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsCommunity.getCreateDate();
        }
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш не доступен, пост не может быть положен в кеш");
            throw new RuntimeException("Кеш не доступен");
        }
        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(),
                postsCommunity.getCommunity().getNickname() ,
                filePostsCommunityService.getFileName(postsCommunity), localDateTime, set, true, postsCommunity.getRating());
        cache.put(namePost, responsePostDTO);
        return  responsePostDTO;
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
        }
        Integer reaction = cache.get(nicknameUser + ':' + responsePostDTO.getNamePost(), Integer.class);
        if(reaction == null){
            reaction = userPostReactionService.getRating(nicknameUser, responsePostDTO.getNamePost());
        }
        return new ResponsePostDTOReaction(responsePostDTO, reaction);
    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostCommunityService-------------------------->
    public Optional<PostsCommunity> findByName(String name) {
        return postsCommunityRepository.findByName(name);
    }


    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @CacheEvict(value = "POST", key = "#deleteCommunityPostDTO.getNamePost()")
    @Transactional
    public void deletePostCommunity(DeleteCommunityPostDTO deleteCommunityPostDTO, String nicknameUser) throws IOException {
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(deleteCommunityPostDTO.getNamePost());
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsCommunity postCommunity = optionalPostsCommunity.get();
        communityService.checkCommunityByNicknameUser(postCommunity.getCommunity(), nicknameUser);
        postCommunity.setCommunity(null);
        filePostsCommunityService.deleteFilePostsCommunityService(postCommunity);
        postsCommunityRepository.delete(postCommunity);
        userPostReactionService.deleteUserReactionByNamePost(deleteCommunityPostDTO.getNamePost());
        kafkaTemplate.send("news-feed-topic-namePost-del", null, postCommunity.getName());
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Transactional
    public ResponsePostDTO createPostCommunity(RequestPostCommunityDTO requestCommunityPostDTO, String nicknameUser, BindingResult bindingResult,
                                               MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        boolean flag = false;
        PostsCommunity postsCommunity = new PostsCommunity();
        postsCommunity.setCommunity(communityService.checkCommunityByNicknameUser(requestCommunityPostDTO.getNicknameCommunity(), nicknameUser));

        if(requestCommunityPostDTO.getTitle() != null){
            postsCommunity.setTitle(requestCommunityPostDTO.getTitle());
            flag = true;
        }
        if(requestCommunityPostDTO.getDescription() != null){
            postsCommunity.setDescription(requestCommunityPostDTO.getDescription());
            flag = true;
        }
        if(multipartFiles!=null) {
            filePostsCommunityService.createFIlesForPosts(multipartFiles, postsCommunity);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Не переданы необходимые параметры для создания поста сообщества");
        }
        postsCommunity.generateName();
        postsCommunity.setRating(0L);
        postsCommunityRepository.save(postsCommunity);
        kafkaTemplate.send("news-feed-topic-community", requestCommunityPostDTO.getNicknameCommunity(), postsCommunity.getName());
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), LocalDateTime.now(), false, true, 0L);
    }
    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Transactional
    public ResponsePostDTO setPostCommunnity(SetPostCommunityDTO setCommunityPostDTO, String nicknameUser, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(setCommunityPostDTO.getNamePost());
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsCommunity postsCommunity = optionalPostsCommunity.get();
        Community community = postsCommunity.getCommunity();
        communityService.checkCommunityByNicknameUser(community, nicknameUser);
        if(setCommunityPostDTO.getTitle() != null) {
            postsCommunity.setTitle(setCommunityPostDTO.getTitle());
        }
        postsCommunity.setDescription(setCommunityPostDTO.getDescription());
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        if(multipartFiles!=null) {
            filePostsCommunityService.createFIlesForPosts(multipartFiles, postsCommunity);
        }
        postsCommunity.setUpdateDate(LocalDateTime.now());
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), postsCommunity.getUpdateDate(), true, true, postsCommunity.getRating());
    }
}
