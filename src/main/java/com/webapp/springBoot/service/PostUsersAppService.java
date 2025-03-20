package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.UsersPost.RequestUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceListUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceUsersPostDTO;
import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        filePostsUsersAppService.deleteFileTapeUsersAppService(findByName(namePost));
    }

    // <------------------------ СОЗДАНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    public void createPostUsersApp(RequestUsersPostDTO requestUsersPostDTO, BindingResult result,
            MultipartFile[] multipartFiles) throws ValidationErrorWithMethod, IOException {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        PostsUserApp postsUserApp = new PostsUserApp();
        postsUserApp.setUsersApp(usersService.findUsersByNickname(requestUsersPostDTO.getNicknameUser()));
        postsUserApp.setTitle(requestUsersPostDTO.getTitle());
        postsUserApp.generateName();
        postsUserApp.setDescription(requestUsersPostDTO.getDescription());
        filePostsUsersAppService.createFIlesForPosts(multipartFiles, postsUserApp);
        postsUsersAppRepository.save(postsUserApp);
    }
}
