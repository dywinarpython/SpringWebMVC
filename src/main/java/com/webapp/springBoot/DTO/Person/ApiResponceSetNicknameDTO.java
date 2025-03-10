package com.webapp.springBoot.DTO.Person;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;


@Schema(description = "Работа с изменением поле nickname DOT")
public class ApiResponceSetNicknameDTO {
    @Schema(example = "nickname")
    private String nicknameBefore;

    @Schema(example = "nickname1")
    @Pattern(regexp = "[a-zA-z0-9]*$", message = "Nickname должен состоять из букв латинского языка (без спец. символов)")
    @Pattern(regexp = "^\\D{2}.*", message = "Первые 2 символа nickname не могут быть цифрами")
    private String nicknameAfter;

    public String getNicknameBefore() {
        return nicknameBefore;
    }

    public void setNicknameBefore(String nicknameBefore) {
        this.nicknameBefore = nicknameBefore;
    }

    public String getNicknameAfter() {
        return nicknameAfter;
    }

    public void setNicknameAfter(String nicknameAfter) {
        this.nicknameAfter = nicknameAfter;
    }
}
