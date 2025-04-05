package com.webapp.springBoot.DTO.Meneger;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
@Schema(description = "Сущность бана пользвоателя")
public class BanUsersDTO {

    @NotNull
    @Schema(example = "nickname")
    private  String nickname;


    private Long time;


    private Boolean banForEver;
}
