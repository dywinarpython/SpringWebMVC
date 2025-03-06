package com.webapp.springBoot.DTO;

import com.webapp.springBoot.entity.Users;

import java.util.List;

public class ApiResponceListUsersDOTO {
    private List<Users> usersList;

    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }
}
