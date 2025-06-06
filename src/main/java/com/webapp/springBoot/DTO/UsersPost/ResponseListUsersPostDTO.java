package com.webapp.springBoot.DTO.UsersPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов")
public class ResponseListUsersPostDTO {
    private List<ResponseUsersPostDTO> usersPosts;
}
