package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
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
import java.time.ZoneId;
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

    @Transactional(readOnly = true)
    public Long getUserIdForCommunityNickname(String nickname){
        return communityService.findCommunityByNickname(nickname).getUserOwner().getId();
    }


    @Cacheable(value = "COMMUNITY_POST_LIST")
    @Transactional(readOnly = true)
    public ResponseListPostDTO getPostsByNickname(String nickname){
        Community community = communityService.findCommunityByNickname(nickname);
        List<PostsCommunity> postsCommunityList = community.getPostsCommunityList();
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
                            set
                    ));
                }
        );
        return new ResponseListPostDTO(responseCommunityPostDTO);
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
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), localDateTime, set);
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
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), localDateTime, set);
    }

    // <------------------------ ПОИСК В СУЩНОСТИ PostCommunityService-------------------------->
    public Optional<PostsCommunity> findByName(String name) {
        return postsCommunityRepository.findByName(name);
    }


    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Caching(evict = {
            @CacheEvict(value = "POST", key = "#deleteCommunityPostDTO.getNamePost()"),
            @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#deleteCommunityPostDTO.getNickname()")
    })
    @Transactional
    public void deletePostCommunity(DeleteCommunityPostDTO deleteCommunityPostDTO, String nicknameUser) throws IOException {
        Optional<PostsCommunity>  optionalPostsCommunity = findByName(deleteCommunityPostDTO.getNamePost());
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        PostsCommunity postsCommunity = optionalPostsCommunity.get();
        communityService.checkCommunityByNicknameUser(postsCommunity.getCommunity(), nicknameUser);
        postsCommunity.setCommunity(null);
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        postsCommunityRepository.delete(postsCommunity);
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#requestCommunityPostDTO.getNicknameCommunity()")
    @CachePut(value = "POST" , key = "#result.getNamePost()")
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
        postsCommunityRepository.save(postsCommunity);
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), LocalDateTime.now(), false);
    }

    @CacheEvict(value = "COMMUNITY_POST_LIST", key = "#result.getNicknameCommunity()")
    @CachePut(value = "POST" , key = "#result.getNamePost()")
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
        List<String> fileNames;
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        if(multipartFiles!=null) {
            filePostsCommunityService.createFIlesForPosts(multipartFiles, postsCommunity);
        }
        postsCommunity.setUpdateDate(LocalDateTime.now());
        return new ResponsePostDTO(postsCommunity.getTitle(), postsCommunity.getDescription(),postsCommunity.getName(), postsCommunity.getCommunity().getNickname() , filePostsCommunityService.getFileName(postsCommunity), postsCommunity.getUpdateDate(), true);

    }
}
