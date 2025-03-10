package com.webapp.springBoot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Сущность пользователя")
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(length = 15)
    private String name;

    @NotNull
    @Column(length = 20)
    private String surname;

    @NotNull
    private int age;


    @NotNull
    @Column(length = 20, unique = true)
    private String nickname;


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

    public int getAge() {
        return age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Users(String name, String surname, int age, String nickname) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.nickname = nickname;

    }
    public Users() {

    }



}
