package com.webapp.springBoot.DTO.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ListCommunityUsersDTO {
    private List<CommunityUsersDTO> communityUsersDTOList;

}
