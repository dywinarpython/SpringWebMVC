package com.webapp.springBoot.DTO.Community;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Изменение имени сообщества")
public class SetNameCommunityDTO {
    @Schema(example = "nickname")
    private String nickname;

    @Schema(example = "Сообщество 1")
    @Size(max = 255, min = 10)
    private String name;
}
