package com.webapp.springBoot.DTO.UserReaction;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserReactionDTO {

    @NotNull
    private String namePost;

    private Integer reaction;
}
