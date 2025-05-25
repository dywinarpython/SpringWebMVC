package com.webapp.springBoot.DTO.Post;

import com.webapp.springBoot.entity.PostsUserApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов пользователя")
public class ResponsePostDTO {


    private String title;

    private String description;


    private String nickname;

    private String namePost;

    private List<String> namePostsFile;

    private Long localDateTime;

    private Boolean set;

    private Boolean communityBol;

    private Long rating;


    public ResponsePostDTO(String title, String description, String namePost , String nickname, List<String> namePostsFile, LocalDateTime localDateTime, Boolean set, Boolean communityBol, Long rating){
        this.title = title;
        this.description = description;
        this.nickname = nickname;
        this.namePostsFile = namePostsFile;
        this.namePost = namePost;
        this.localDateTime = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        this.set = set;
        this.communityBol = communityBol;
        this.rating = rating;
    }

}
