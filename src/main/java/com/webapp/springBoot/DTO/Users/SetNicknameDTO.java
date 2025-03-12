package com.webapp.springBoot.DTO.Users;

import com.webapp.springBoot.validation.Person.Unique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Работа с изменением поле nickname DOT")
public class SetNicknameDTO {
    @Schema(example = "nickname")
    private String nicknameBefore;

    @Unique
    @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
    @Schema(example = "nickname1")
    @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
    private String nicknameAfter;
}
