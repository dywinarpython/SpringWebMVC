package com.webapp.springBoot.DTO.Person;

import com.webapp.springBoot.entity.UsersApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Лист сущностей пользователей")
public class ListUsersDTO {
    private List<UsersApp> userList;
}
