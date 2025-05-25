package com.webapp.springBoot.DTO.UserReaction;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RequestUserReactionDTO {

    @NotNull
    private String namePost;

    @Min(value = -1, message = "Минимаьное допустимое значение оценки -1")
    @Max(value = 5, message = "Максимальное допустимое значение оценки 5")
    @NotNull
    private Integer reaction;
}
