package com.webapp.springBoot.DTO.OAuth2;

import com.webapp.springBoot.validation.UsersApp.Unique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Schema(description = "Сущность создания пользователя OAuth2")
public class UserRequestOAuth2DTO {

    @Schema( example = "Иван")
    @Size(min = 2, max = 15, message = "Длина имени от 2 до 15")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Name должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа name не могут быть цифрами")
    private String name;


    @Schema( example = "Иванов")
    @Size(min = 2, max = 20, message = "Длина фамилии от 2 до 20")
    @Pattern(regexp = "[A-ZА-ЯЁё][a-zа-яё]*$", message = "Surname должен состоять из букв латинского, русского языка (без спец. символов и цифр), только первая буква большая")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа surname не могут быть цифрами")
    private String surname;

    @NotNull(message = "age не может быть null")
    @Schema( example = "19")
    @Min(value = 14, message = "Зарегистрироваться можно только после 14 лет")
    @Max(value = 100, message = "Возраст должен быть не больше 100")
    private Integer age;

    @NotNull(message = "nickname не может быть null")
    @Schema( example = "nickname")
    @Unique(message = "Nickname должен быть уникальным")
    @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
    @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
    private String nickname;

    @NotNull(message = "password не может быть null")
    @Size(min = 8, max = 12, message = "Пароль должен содержать не менее 8 символов, и не более 12 симолов")
    @Schema(example = "HIUYvfh@1345")
    @Pattern(regexp = ".*[a-z].*", message = "Пароль должен содержать хотя бы одну строчную букву")
    @Pattern(regexp = ".*[A-Z].*", message = "Пароль должен содержать хотя бы одну заглавную букву")
    @Pattern(regexp = ".*\\d.*", message = "Пароль должен содержать хотя бы одну цифру")
    @Pattern(regexp = ".*[!@#$%^&*?/].*", message = "Пароль должен содержать хотя бы один специальный символ (!@#$%^&*?/)")
    private String password;



}
