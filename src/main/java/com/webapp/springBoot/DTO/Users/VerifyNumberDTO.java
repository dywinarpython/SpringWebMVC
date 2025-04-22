package com.webapp.springBoot.DTO.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "Сущность подтверждения номера телефона")
public class VerifyNumberDTO {

    @NotNull
    private String code;
}
