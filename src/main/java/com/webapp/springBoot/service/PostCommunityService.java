package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.DTO.UserReaction.ListUserReactionDTO;
import com.webapp.springBoot.DTO.UserReaction.UserReactionDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.PostsCommunityRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    private ResponseListPostDTO getPostsForCommunity(String nickname, Integer page){
        List<PostsCommunity> postsCommunityList = postsCommunityRepository.findByCommunityNickname(nickname, PageRequest.of(page, 5));
        return getPostWithPostList(postsCommunityList);
    }

    private ResponseListPostDTO getPostWithPostList(List<PostsCommunity> postsCommunityList){
        List<ResponsePostDTO> responsePostDTOList = new ArrayList<>();
        postsCommunityList.forEach(postsCommunity -> {
            responsePostDTOList.add(getPost(postsCommunity));
        });
        return new ResponseListPostDTO(responsePostDTOList);
    }

    @Transactional(readOnly = true)
    public ResponseListPostDTOReaction getPostsByNicknameReaction(String nickname, int page, String nicknameGet) {
        ResponseListPostDTO responseListPostDTO = getPostsForCommunity(nickname,page);
        ListUserReactionDTO userPostReactionsDto = userPostReactionService.getRating(nicknameGet,
                responseListPostDTO.getPosts().stream()
                        .map(ResponsePostDTO::getNamePost
                        ).toList()
        );
        Map<String, Integer> integerMap = userPostReactionsDto.getUserReactionDTO()
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

    public ResponseEntity<Resource> getFilePost(String nameFile) throws IOException {
        String fileStringPath = filePostsCommunityService.getFile(nameFile);
        String contentType = Files.probeContentType(Path.of(fileStringPath));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileStringPath));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(resource);
    }
    private ResponsePostDTO getPost(PostsCommunity postsCommunity){
        Cache cache = cacheManager.getCache("POST");
        if(cache == null){
            log.error("Кеш для записи поста не доступен");
            throw new RuntimeException("Кеш не дотсупен");
        }
        boolean set;
        LocalDateTime localDateTime;
        if(postsCommunity.getUpdateDate() != null){
            set = true;
            localDateTime = postsCommunity.getUpdateDate();
        } else {
            set = false;
            localDateTime = postsCommunity.getCreateDate();
        }
        ResponsePostDTO responsePostDTO = new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), localDateTime, set, true, postsCommunity.getRating());
        cache.put(postsCommunity.getName(), responsePostDTO);
        return responsePostDTO;
    }

    public ResponsePostDTO getPostNull(String namePost){
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(namePost);
        if (optionalPostsCommunity.isEmpty()) {
            return null;
        }
        PostsCommunity postsCommunity = optionalPostsCommunity.get();
        return getPost(postsCommunity);
    }

    private ResponsePostDTO getPostWithName(String namePost){
        Optional<PostsCommunity> optionalPostsUserApp = findByName(namePost);
        if (optionalPostsUserApp.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsCommunity postsCommunity = optionalPostsUserApp.get();
        return getPost(postsCommunity);
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
