package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Setter
@Schema(description = "Сущность пользователя")
@Entity
public class UsersApp {

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

    @JsonIgnore
    @OneToMany(mappedBy = "userOwnerId", cascade = CascadeType.ALL)
    private List<Community> community;


    public UsersApp(String name, String surname, int age, String nickname) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.nickname = nickname;


    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Community> getCommunity() {
        return community;
    }
}
