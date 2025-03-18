package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.PostsUserApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.PostsUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PostUsersAppService {
    @Autowired
    private FilePostsUsersAppService filePostsUsersAppService;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private PostsUsersAppRepository postsUsersAppRepository;

    // <------------------------ ПОИСК В СУЩНОСТИ PostUsersAppService-------------------------->
    public PostsUserApp findByName(String name){
        Optional<PostsUserApp> optionalPostsUserApp = postsUsersAppRepository.findByName(name);
        if(optionalPostsUserApp.isEmpty()){
            throw new NoSuchElementException("Пост не найден");
        }
        return optionalPostsUserApp.get();
    }

    // <------------------------ УДАЛЕНИЕ В СУЩНОСТИ PostUsersAppService-------------------------->
    @Transactional
    public void deletePostUsersApp(String namePost) throws IOException {
        filePostsUsersAppService.deleteFileTapeUsersAppService(findByName(namePost));
    }


}
