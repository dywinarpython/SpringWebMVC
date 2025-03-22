package com.webapp.springBoot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Schema(description = "Сущность загрузки файлов сообщества (лента)")
public class PostsCommunityFile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String nameFile;

    @NotNull
    private String fileUrl;


    public PostsCommunityFile() {
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
