package com.webapp.springBoot.DTO.Users;


import com.webapp.springBoot.entity.UsersApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Сущность вывода пользователя")
public class UserResponceDTO {


    private String name;

    private String surname;

    private int age;


    private String nickname;

    private String nameImage;

    public UserResponceDTO(UsersApp usersApp, String nameImage){
        this.name = usersApp.getName();
        this.surname = usersApp.getSurname();
        this.age = usersApp.getAge();
        this.nickname = usersApp.getNickname();
        this.nameImage = nameImage;
    }


}
