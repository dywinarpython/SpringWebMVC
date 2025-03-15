package com.webapp.springBoot.DTO.Community;


import com.webapp.springBoot.validation.Community.UniqueCommunity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Сущность создания сообщества")
public class CommunityResponceDTO {

    private String name;



    private String description;


    private String nicknameUser;

    private String nicknameCommunity;

    private String nameImage;

}
