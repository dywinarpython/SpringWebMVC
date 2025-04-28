package com.webapp.springBoot.DTO.Friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Сущность friend")
@Getter
public class ResponseFriendDTO {

    private String nickname;

    private String name;

    private String surname;

    private String nameFile;
}
