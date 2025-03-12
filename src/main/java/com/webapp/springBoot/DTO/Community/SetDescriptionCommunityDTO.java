package com.webapp.springBoot.DTO.Community;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Изменение описания сообщества")
public class SetDescriptionCommunityDTO {
    @Schema(example = "nickname")
    private String nickname;

    @Schema(example = "ОФициальное сообщество веб-приложения!")
    @Size(max = 255, min = 10)
    private String description;
}
