package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.CommunityPost.*;
import com.webapp.springBoot.DTO.UsersPost.ResponseUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.SetUsersPostDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.PostsCommunityRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    // <------------------------ ПОЛУЧЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Cacheable(value = "COMMUNITY_POST_LIST", key = "#nickname")
    @Transactional(readOnly = true)
    public ResponseListCommunityPostDTO getPostsByNickname(String nickname){
        Community community = communityService.findCommunityByNickname(nickname);
        List<PostsCommunity> postsCommunityList = community.getPostsCommunityList();
        List<ResponseCommunityPostDTO> responseCommunityPostDTO = new ArrayList<>();
        postsCommunityList.forEach(postsCommunity ->
                        responseCommunityPostDTO.add(new ResponseCommunityPostDTO(
                                postsCommunity.getTitle(),
                                postsCommunity.getDescription(),
                                nickname,
                                postsCommunity.getName(),
                                filePostsCommunityService.getFileName(postsCommunity)
                ))
                );
        return new ResponseListCommunityPostDTO(responseCommunityPostDTO);
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
    @Cacheable(value = "COMMUNITY_POST", key = "#namePost")
    public ResponseCommunityPostDTO getPost(String namePost){
        PostsCommunity postsCommunity = findByName(namePost);
        return new ResponseCommunityPostDTO(postsCommunity, postsCommunity.getCommunity().getNickname(), filePostsCommunityService.getFileName(postsCommunity));
    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostCommunityService-------------------------->
    public PostsCommunity findByName(String name) {
        Optional<PostsCommunity> optionalPostsCommunity = postsCommunityRepository.findByName(name);
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        return optionalPostsCommunity.get();
    }


    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Caching(evict = {
            @CacheEvict(value = "COMMUNITY_POST", key = "#namePost"),
            @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#deleteCommunityPostDTO.getNickname()")
    })
    @Transactional
    public void deletePostCommunity(DeleteCommunityPostDTO deleteCommunityPostDTO, String nicknameUser) throws IOException {
        PostsCommunity postsCommunity = findByName(deleteCommunityPostDTO.getNamePost());
        communityService.checkCommunityByNicknameUser(postsCommunity.getCommunity(), nicknameUser);
        postsCommunity.setCommunity(null);
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        postsCommunityRepository.delete(postsCommunity);
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#requestCommunityPostDTO.getNicknameCommunity()")
    @CachePut(value = "COMMUNITY_POST" , key = "#result.getNamePost()")
    @Transactional
    public ResponseCommunityPostDTO createPostCommunity(RequestCommunityPostDTO requestCommunityPostDTO, String nicknameUser, BindingResult bindingResult,
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
        return new ResponseCommunityPostDTO(postsCommunityRepository.save(postsCommunity),requestCommunityPostDTO.getNicknameCommunity(), filePostsCommunityService.getFileName(postsCommunity));
    }

    @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#result.getNicknameCommunity()")
    @CachePut(value = "COMMUNITY_POST" , key = "#result.getNamePost()")
    @Transactional
    public ResponseCommunityPostDTO setPostCommunnity(SetCommunityPostDTO setCommunityPostDTO, String nicknameUser, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsCommunity postsCommunity = findByName(setCommunityPostDTO.getNamePost());
        Community community = postsCommunity.getCommunity();
        communityService.checkCommunityByNicknameUser(community, nicknameUser);
        if(setCommunityPostDTO.getTitle() != null) {
            postsCommunity.setTitle(setCommunityPostDTO.getTitle());
        }
        postsCommunity.setDescription(setCommunityPostDTO.getDescription());
        List<String> fileNames;
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        if(multipartFiles!=null) {
            filePostsCommunityService.createFIlesForPosts(multipartFiles, postsCommunity);
            fileNames = filePostsCommunityService.getFileName(postsCommunity);
        } else {
            fileNames = null;
        }
        postsCommunity.setUpdateDate(LocalDateTime.now());
        return new ResponseCommunityPostDTO(postsCommunityRepository.save(postsCommunity), community.getNickname(), fileNames);
    }
}
