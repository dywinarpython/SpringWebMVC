package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Schema(description = "Сущность загрузки изображений пользователя")
public class ImagesUsersApp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Getter
    @NotNull
    private String nameImage;

    @Getter
    @Setter
    @NotNull
    private String imageUrl;



    public ImagesUsersApp() {
        nameImage = UUID.randomUUID().toString();
    }

}
