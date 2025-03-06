package com.webapp.springBoot.DTO.Person;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Сущность пользователя")
public class APiResponceUserDTO {

    @Size(min = 2, max = 15, message = "Длина имени от 2 до 15")
    private String name;

    @Size(min = 2, max = 20, message = "Длина фамилии от 2 до 20")
    private String surname;


    @Min(value = 14, message = "Зарегистрироваться можно только после 14 лет")
    @Max(value = 120, message = "Возраст должен быть не больше 120")
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
