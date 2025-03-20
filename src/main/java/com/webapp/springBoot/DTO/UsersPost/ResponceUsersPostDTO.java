package com.webapp.springBoot.DTO.UsersPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов")
public class ResponceUsersPostDTO {


    private String title;

    private String description;


    private String nicknameUser;

    private String namePost;

    private List<String> namePostsFile;

}
