package com.webapp.springBoot.DTO.Meneger;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Schema(description = "Сущность бана пользвоателя")
@NoArgsConstructor
public class BanUsersDTO {

    @NotNull
    @Schema(example = "nickname")
    private  String nickname;


    private Long time;


    private Boolean banForEver;
}
