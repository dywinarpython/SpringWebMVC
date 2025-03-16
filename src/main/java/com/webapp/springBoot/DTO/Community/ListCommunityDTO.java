package com.webapp.springBoot.DTO.Community;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "Лист сущностей сообществ")
public class ListCommunityDTO {
    private List<CommunityResponseDTO> communityList;
}
