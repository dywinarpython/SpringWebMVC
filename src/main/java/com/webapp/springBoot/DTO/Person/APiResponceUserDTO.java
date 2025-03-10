package com.webapp.springBoot.DTO.Person;


import com.webapp.springBoot.validation.Person.Unique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Сущность пользователя")
public class APiResponceUserDTO {

    @Schema( example = "Иван")
    @Size(min = 2, max = 15, message = "Длина имени от 2 до 15")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа name не могут быть цифрами")
    private String name;

    @Schema( example = "Иванов")
    @Size(min = 2, max = 20, message = "Длина фамилии от 2 до 20")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа surname не могут быть цифрами")
    private String surname;

    @Schema( example = "19")
    @Min(value = 14, message = "Зарегистрироваться можно только после 14 лет")
    @Max(value = 100, message = "Возраст должен быть не больше 100")
    private int age;

    @Schema( example = "nickname")
    @Unique(message = "Nickname должен быть уникальным")
    @Size(min = 2, max = 10,  message = "Длина nickname от 2 до 10")
    @Pattern(regexp = "[a-zA-z0-9]*$", message = "Nickname должен состоять из букв латинского языка (без спец. символов)")
    @Pattern(regexp = "^\\D{2}.*", message = "Первые 2 символа nickname не могут быть цифрами")
    private String nickname;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
