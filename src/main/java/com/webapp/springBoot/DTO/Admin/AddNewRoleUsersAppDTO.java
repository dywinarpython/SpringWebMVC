package com.webapp.springBoot.DTO.Admin;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "Изменение прав пользователя")
public class AddNewRoleUsersAppDTO {

    @NotNull
    @Schema(example = "nickname")
    private String nickname;

    @NotNull
    @Schema(example = "ADMIN")
    private String nameRole;
}
