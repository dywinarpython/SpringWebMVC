package com.webapp.springBoot.DTO.Community;

import com.webapp.springBoot.entity.Community;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Лист сущностей сообществ")
public class ListCommunityDTO {
    private List<Community> communityList;
}
