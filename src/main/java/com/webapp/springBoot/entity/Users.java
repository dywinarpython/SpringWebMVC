package com.webapp.springBoot.entity;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
@Schema(description = "Сущность пользователя")
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(length = 15)
    private String name;

    @Column(length = 20)
    private String surname;

    private int age;


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

    public Users(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }
    public Users() {

    }
}
