package com.webapp.springBoot.DTO.Users;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Вывод json формата сообществ пользователя")
public class CommunityUsersDTO {
    private String name;

    private String description;

    private String nicknameCommunity;
}
