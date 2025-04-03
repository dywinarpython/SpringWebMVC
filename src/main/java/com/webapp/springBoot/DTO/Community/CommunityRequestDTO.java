package com.webapp.springBoot.DTO.Community;



import com.webapp.springBoot.validation.Community.UniqueCommunity;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Сущность создания сообщества")
public class CommunityRequestDTO {

    @Size(max = 20, min = 2)
    @Schema(example = "Сообщество Messages")
    @Pattern(regexp = "[a-zA-zа-яА-яёЁ0-9 ]*$", message = "Name должен состоять из букв латинского, русского языка (без спец. символов)")
    @Pattern(regexp = "^\\D{2}.*$", message = "Первые 2 символа name не могут быть цифрами")
    private String name;


    @Schema(example = "ОФициальное сообщество веб-приложения!")
    @Size(max = 255, min = 10)
    private String description;

    @Schema( example = "nickname")
    @UniqueCommunity(message = "NicknameCommunity должен быть уникальным")
    @Size(min = 2, max = 20,  message = "Длина сообщества nickname от 2 до 10")
    @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-z0-9]*$", message = "Nickname сообщества должен состоять из букв латинского языка (без спец. символов), первые 2 символа nickname не могут быть цифрами")
    private String nicknameCommunity;

}
