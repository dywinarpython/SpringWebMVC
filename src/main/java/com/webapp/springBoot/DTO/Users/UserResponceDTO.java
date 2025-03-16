package com.webapp.springBoot.DTO.Users;


import com.webapp.springBoot.entity.ImagesUsersApp;
import com.webapp.springBoot.validation.Person.Unique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Сущность вывода пользователя")
public class UserResponceDTO {


    private String name;

    private String surname;

    private int age;


    private String nickname;

    private String nameImage;

}
