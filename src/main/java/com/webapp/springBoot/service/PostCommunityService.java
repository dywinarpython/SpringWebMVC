package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.CommunityPost.RequestCommunityPostDTO;
import com.webapp.springBoot.DTO.CommunityPost.ResponceCommunityPostDTO;
import com.webapp.springBoot.DTO.CommunityPost.ResponceListCommunityPostDTO;
import com.webapp.springBoot.DTO.CommunityPost.SetCommunityPostDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.PostsCommunityRepository;
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
    @Transactional
    public ResponceListCommunityPostDTO getPostsByNickname(String nickname){
        Community community = communityService.findCommunityByNickname(nickname);
        List<PostsCommunity> postsCommunityList = community.getPostsCommunityList();
        List<ResponceCommunityPostDTO> responceCommunityPostDTO = new ArrayList<>();
        postsCommunityList.forEach(postsCommunity ->
                        responceCommunityPostDTO.add(new ResponceCommunityPostDTO(
                                postsCommunity.getTitle(),
                                postsCommunity.getDescription(),
                                nickname,
                                postsCommunity.getName(),
                                filePostsCommunityService.getFileName(postsCommunity)
                ))
                );
        return new ResponceListCommunityPostDTO(responceCommunityPostDTO);
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

    // <------------------------ ПОИСК В СУЩНОСТИ PostCommunityService-------------------------->
    public PostsCommunity findByName(String name) {
        Optional<PostsCommunity> optionalPostsCommunity = postsCommunityRepository.findByName(name);
        if (optionalPostsCommunity.isEmpty()) {
            throw new NoSuchElementException("Пост не найден");
        }
        return optionalPostsCommunity.get();
    }

    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Transactional
    public void deletePostCommunity(String namePost) throws IOException {
        PostsCommunity postsCommunity = findByName(namePost);
        postsCommunity.setCommunity(null);
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        postsCommunityRepository.delete(postsCommunity);
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostCommunityService-------------------------->
    @Transactional
    public void createPostCommunity(RequestCommunityPostDTO requestCommunityPostDTO, BindingResult result,
                                    MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        boolean flag = false;
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsCommunity postsCommunity = new PostsCommunity();

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
        postsCommunity.setCommunity(communityService.findCommunityByNickname(requestCommunityPostDTO.getNicknameCommunity()));
        postsCommunity.generateName();
        postsCommunityRepository.save(postsCommunity);
    }
    @Transactional
    public void setPostCommunnity(SetCommunityPostDTO setCommunityPostDTO, BindingResult result, MultipartFile[] multipartFiles) throws IOException, ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        boolean flag = false;
        PostsCommunity postsCommunity = findByName(setCommunityPostDTO.getNamePost());
        filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
        if(setCommunityPostDTO.getTitle() != null) {
            postsCommunity.setTitle(setCommunityPostDTO.getTitle());
            flag = true;
        }
        if(setCommunityPostDTO.getDescription() != null){
            postsCommunity.setDescription(setCommunityPostDTO.getDescription());
            flag = true;
        }
        postsCommunity.setUpdateDate(LocalDateTime.now());
        if(multipartFiles!=null) {
            filePostsCommunityService.createFIlesForPosts(multipartFiles, postsCommunity);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
        postsCommunityRepository.save(postsCommunity);
    }
}
