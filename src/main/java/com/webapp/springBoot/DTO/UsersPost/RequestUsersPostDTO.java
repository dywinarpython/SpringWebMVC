package com.webapp.springBoot.DTO.UsersPost;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RequestUsersPostDTO {


    @Schema(description = "Title поста пользователя")
    @Size(max = 60, min = 3)
    private String title;

    @Schema(description = "Description поста пользователя")
    @Size(max = 280)
    private String description;

}
