package com.webapp.springBoot.DTO.CommunityPost;

import com.webapp.springBoot.entity.PostsCommunity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов сообещства")
public class ResponseCommunityPostDTO {

    private String title;

    private String description;

    private String nicknameCommunity;

    private String namePost;

    private List<String> namePostsFile;

    public ResponseCommunityPostDTO(PostsCommunity postsCommunity, String nicknameCommunity, List<String> namePostsFile){
        this.title = postsCommunity.getTitle();
        this.namePost = postsCommunity.getName();
        this.namePostsFile = namePostsFile;
        this.nicknameCommunity = nicknameCommunity;
    }

}
