package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Schema(description = "Сущность загрузки изображений")
public class ImagesCommunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String imageUrl;

    @JsonIgnore
    @OneToOne(mappedBy = "imageUrlId")
    private Community community;

    public ImagesCommunity(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
