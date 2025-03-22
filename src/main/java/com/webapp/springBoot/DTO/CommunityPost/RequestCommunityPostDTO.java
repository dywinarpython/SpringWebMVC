package com.webapp.springBoot.DTO.CommunityPost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов")
public class RequestCommunityPostDTO {
    @NotNull
    @Schema(description = "Title поста сообщества")
    @Size(max = 30, min = 3)
    private String title;

    @Schema(description = "Description поста сообщества")
    @Size(max = 280)
    private String description;

    @Schema(description = "nickname")
    @NotNull
    private String nicknameCommunity;
}
