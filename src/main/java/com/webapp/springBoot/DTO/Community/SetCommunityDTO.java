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

        @UniqueCommunity(message = "Значение nickname сообщество должно быть уникальным")
        @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
        @Schema(example = "community")
        @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
        private String nicknameAfter;

        @Schema(example = "Сообщество 1")
        @Size(max = 255, min = 10)
        private String name;

        @Schema(example = "ОФициальное сообщество веб-приложения!")
        @Size(max = 255, min = 10)
        private String description;


}
