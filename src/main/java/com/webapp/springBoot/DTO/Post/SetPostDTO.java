package com.webapp.springBoot.DTO.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность изменение постов ")
public class SetPostDTO {

    @NotNull(message = "Имя поста не может быть null")
    @Schema(description = "posts_UUID")
    private String namePost;

    @Schema(description = "Title поста ")
    @Size(max = 30, min = 3)
    private String title;

    @Schema(description = "Description поста ")
    @Size(max = 280)
    private String description;

}
