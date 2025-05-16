package com.webapp.springBoot.DTO.Friend;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Сущность друзей пользователя")
public class ListResponseFriendDTO {

    private List<ResponseFriendDTO> users;

}
