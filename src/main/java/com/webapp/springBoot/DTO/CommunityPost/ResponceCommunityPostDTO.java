package com.webapp.springBoot.DTO.CommunityPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов сообещства")
public class ResponceCommunityPostDTO {

    private String title;

    private String description;

    private String nicknameCommunity;

    private String namePost;

    private List<String> namePostsFile;

}
