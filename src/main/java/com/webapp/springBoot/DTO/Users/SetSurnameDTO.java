package com.webapp.springBoot.DTO.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Изменение фамилии пользователя")
public class SetSurnameDTO {

    @Schema(example = "nickname")
    private String nickname;

    @Schema( example = "Иванов")
    @Size(min = 2, max = 20, message = "Длина фамилии от 2 до 20")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Surname должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа surname не могут быть цифрами")
    private String surnameAfter;
}
