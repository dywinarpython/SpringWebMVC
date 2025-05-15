package com.webapp.springBoot.DTO.Post;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@Schema(description = "Сущность удаления поста сообщества")
public class DeleteCommunityPostDTO {

    @NotNull(message = "Nickname сообщества не может быть null")
    private String nickname;

    @NotNull(message = "Имя поста сообщества не может быть null")
    private String namePost;
}
