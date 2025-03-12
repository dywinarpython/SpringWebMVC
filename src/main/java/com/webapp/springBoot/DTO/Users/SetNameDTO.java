package com.webapp.springBoot.DTO.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
@Schema(description = "Изменение имени пользователя")
public class SetNameDTO {

    @Schema(example = "nickname")
    private String nickname;

    @Schema( example = "Иван")
    @Size(min = 2, max = 15, message = "Длина имени от 2 до 15")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Name должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа name не могут быть цифрами")
    private String nameAfter;

}
