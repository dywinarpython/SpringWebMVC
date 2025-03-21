package com.webapp.springBoot.DTO.Users;

import com.webapp.springBoot.validation.Person.Unique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Schema(description = "Работа с изменением сущности UsersApp")
public class SetUserDTO {
    @NotNull(message = "Nickname пользователя не должен равняться null")
    @Schema(example = "nickname")
    private String nickname;

    @Unique
    @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
    @Schema(example = "nickname1")
    @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
    private String nicknameAfter;

    @Schema( example = "Иванов")
    @Size(min = 2, max = 20, message = "Длина фамилии от 2 до 20")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Surname должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа surname не могут быть цифрами")
    private String surname;

    @Schema( example = "Иван")
    @Size(min = 2, max = 15, message = "Длина имени от 2 до 15")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Name должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа name не могут быть цифрами")
    private String name;

}
