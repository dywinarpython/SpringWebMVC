package com.webapp.springBoot.listners;

import com.webapp.springBoot.DTO.Listner.UserHelperDTODelCommunity;
import com.webapp.springBoot.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserHelperListner {

    @Autowired
    private CommunityService communityService;

    @EventListener
    public void deleteCommunity(UserHelperDTODelCommunity userHelperDTODelCommunity) throws IOException {
        communityService.deleteCommunityByNickname(userHelperDTODelCommunity.getNicknameCommunity(), userHelperDTODelCommunity.getNickname(), false);
    }
}
