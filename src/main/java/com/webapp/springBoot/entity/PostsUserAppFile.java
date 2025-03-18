package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Schema(description = "Сущность загрузки изображений пользователя (лента)")
public class PostsUserAppFile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String nameFile;

    @NotNull
    private String fileUrl;

    @JsonIgnore
    @ManyToOne
    private PostsUserApp tapeUserApp;

    public PostsUserAppFile() {
        nameFile = UUID.randomUUID().toString();
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getFileUrl(){
        return fileUrl;
    }

    public void setImageUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
