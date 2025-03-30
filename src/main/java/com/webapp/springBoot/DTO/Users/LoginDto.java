package com.webapp.springBoot.DTO.Users;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "Сущность логина пользователя")
public class LoginDto {

    private String nickname;

    private String password;
}
