package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.FollowersService;
import com.webapp.springBoot.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Управление подписками пользователей")
@RequestMapping("v1/api/followers")
@RestController
public class FollowersController {

    @Autowired
    private FollowersService followersService;

    @Operation(
            summary = "Создания подписки",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    )
    @PostMapping("/{nickname}")
    public ResponseEntity<String> createFollowers(@PathVariable String nickname, Principal principal) throws ValidationErrorWithMethod {
        followersService.createFollowers(principal.getName(), nickname);
        return  ResponseEntity.ok("Пользователь успешно подписался на сообщество");
    }
    @Operation(
            summary = "Удаление подписки",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    )
    @DeleteMapping("/{nickname}")
    public ResponseEntity<String> deleteFriend(@PathVariable String nickname, Principal principal){
        followersService.deleteFollowers(principal.getName(), nickname);
        return  ResponseEntity.ok("Пользователь успешно удален из друзей");
    }
    @Operation(
            summary = "Получение всех подписчиков сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListResponseFriendDTO.class)))
    )
    @GetMapping("/{nickname}")
    public ListResponseFriendDTO getAllFollowers(@PathVariable String  nickname){
        return followersService.getAllFollowers(nickname);
    }
}
