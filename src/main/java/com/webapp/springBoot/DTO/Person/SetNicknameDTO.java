package com.webapp.springBoot.DTO.Person;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Работа с изменением поле nickname DOT")
public class SetNicknameDTO {
    @Schema(example = "nickname")
    private String nicknameBefore;

    @Schema(example = "nickname1")
    @Pattern(regexp = "[a-zA-z0-9]*$", message = "Nickname должен состоять из букв латинского языка (без спец. символов)")
    @Pattern(regexp = "^\\D{2}.*", message = "Первые 2 символа nickname не могут быть цифрами")
    private String nicknameAfter;
}
