package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Schema(description = "Сущность загрузки изображений пользователя")
public class ImagesUsersApp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String nameImage;

    @NotNull
    private String imageUrl;

    @JsonIgnore
    @OneToOne(mappedBy = "imageUrl")
    private UsersApp usersApp;

    public ImagesUsersApp() {
        nameImage = UUID.randomUUID().toString();
    }

    public String getNameImage() {
        return nameImage;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UsersApp getUsersApp() {
        return usersApp;
    }
}
