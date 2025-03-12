package com.webapp.springBoot.DTO.Users;

import com.webapp.springBoot.entity.UsersApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
@Schema(description = "Лист сущностей пользователей")
public class ListUsersDTO {
    private List<UsersApp> userList;
}
