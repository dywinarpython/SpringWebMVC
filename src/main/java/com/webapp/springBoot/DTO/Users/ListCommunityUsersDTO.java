package com.webapp.springBoot.DTO.Users;

import com.webapp.springBoot.DTO.Community.CommunityResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ListCommunityUsersDTO {
    private List<CommunityResponseDTO> communityUsersDTOList;

}
