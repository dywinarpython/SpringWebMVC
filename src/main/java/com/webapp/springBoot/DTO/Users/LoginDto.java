package com.webapp.springBoot.DTO.Users;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "Сущность логина пользователя")
public class LoginDto {

    @NotNull(message = "Nickname должен быть указан")
    private String nickname;

    @NotNull(message = "Пароль должен быть указан")
    private String password;
}
