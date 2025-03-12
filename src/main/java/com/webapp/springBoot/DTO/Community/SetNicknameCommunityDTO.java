package com.webapp.springBoot.DTO.Community;

import com.webapp.springBoot.validation.Community.UniqueCommunity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Изменение nickname сообщества")
public class SetNicknameCommunityDTO {
        @Schema(example = "nicknamecommunity")
        private String nicknameBefore;

        @UniqueCommunity(message = "Значени nickname сообщество должно быть уникальным")
        @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
        @Schema(example = "nicknamecommunity2")
        @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
        private String nicknameAfter;

}
