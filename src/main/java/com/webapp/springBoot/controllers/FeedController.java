package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Post.ResponseListPostDTO;
import com.webapp.springBoot.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping("/v1/api/feed")
@RestController
public class FeedController {

    @Autowired
    private FeedService feedService;
    @GetMapping("/{page}")
    public ResponseListPostDTO getFeed(@PathVariable Integer page, Principal principal){
        return feedService.getFeed(principal.getName(), page);
    }
}
