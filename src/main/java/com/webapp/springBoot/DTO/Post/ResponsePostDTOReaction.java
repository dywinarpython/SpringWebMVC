package com.webapp.springBoot.DTO.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов пользователя")
public class ResponsePostDTOReaction {


    private ResponsePostDTO responsePostDTO;

    private Integer reaction;

    public ResponsePostDTOReaction(ResponsePostDTO responsePostDTO) {
        this.responsePostDTO = responsePostDTO;
    }
}
