package com.webapp.springBoot.DTO.UsersPost;

import com.webapp.springBoot.entity.PostsUserApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов пользователя")
public class ResponseUsersPostDTO {


    private String title;

    private String description;


    private String nicknameUser;

    private String namePost;

    private List<String> namePostsFile;

    public ResponseUsersPostDTO(PostsUserApp postsUserApp, String nikcnameUser, List<String> namePostsFile){
        this.title = postsUserApp.getTitle();
        this.description = postsUserApp.getDescription();
        this.nicknameUser = nikcnameUser;
        this.namePostsFile = namePostsFile;
        this.namePost = postsUserApp.getName();
    }

}
