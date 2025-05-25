package com.webapp.springBoot.DTO.Community;

import com.webapp.springBoot.validation.Community.UniqueCommunity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="Работа с изменением сущности Community")
public class SetCommunityDTO {
        @NotNull(message = "Nickname сообщества не должен равняться null")
        @Schema(example = "nickname")
        private String nickname;

        @Schema(example = "Сообщество 1")
        @Size(max = 255, min = 10)
        private String name;

        @Schema(example = "ОФициальное сообщество веб-приложения!")
        @Size(max = 255, min = 10)
        private String description;


}
