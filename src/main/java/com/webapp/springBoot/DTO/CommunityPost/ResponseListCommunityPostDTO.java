package com.webapp.springBoot.DTO.CommunityPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов")
public class ResponseListCommunityPostDTO {
    private List<ResponseCommunityPostDTO> communityPosts;
}
