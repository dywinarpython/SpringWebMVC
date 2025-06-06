package com.webapp.springBoot.DTO.Community;


import com.webapp.springBoot.entity.Community;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "Сущность вывода сообщества")
public class CommunityResponseDTO {

    private String name;



    private String description;


    private String nicknameUser;

    private String nicknameCommunity;

    private String nameImage;

    public CommunityResponseDTO(Community community, String nameImage){
        this.name = community.getName();
        this.description = community.getDescription();
        this.nicknameUser = community.getNickname();
        this.nameImage = nameImage;
        this.nicknameCommunity = community.getNickname();
    }

}
