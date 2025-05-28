package com.webapp.springBoot.DTO.Admin;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Изменение прав пользователя")
public class AddNewRoleUsersAppDTO {

    @NotNull
    @Schema(example = "nickname")
    private String nickname;

    @NotNull
    @Schema(example = "ADMIN")
    private String nameRole;
}
