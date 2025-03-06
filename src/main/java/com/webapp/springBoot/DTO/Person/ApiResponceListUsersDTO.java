package com.webapp.springBoot.DTO.Person;

import com.webapp.springBoot.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Threshold;

import java.util.List;

@Schema(description = "Лист сущностей пользователей")
public class ApiResponceListUsersDTO {
    private List<Users> usersList;

    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }
}
