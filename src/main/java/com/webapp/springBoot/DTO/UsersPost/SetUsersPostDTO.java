package com.webapp.springBoot.DTO.UsersPost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность изменение постов")
public class SetUsersPostDTO {

    private String namePost;

    private String title;

    private String description;

}
