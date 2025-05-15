package com.webapp.springBoot.DTO.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность создания постов")
public class RequestPostDTO {


    @Schema(description = "Title поста ")
    @Size(max = 60, min = 3)
    private String title;

    @Schema(description = "Description поста")
    @Size(max = 280)
    private String description;

}
