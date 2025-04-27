package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Schema(description = "Сущность загрузки изображений сообщества")
public class ImagesCommunity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Getter
    @NotNull
    private String nameImage;

    @Setter
    @Getter
    @NotNull
    private String imageUrl;


    public ImagesCommunity() {
        nameImage = UUID.randomUUID().toString();
    }

}
