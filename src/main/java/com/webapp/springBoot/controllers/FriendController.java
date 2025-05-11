package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
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

@Tag(name = "Управление друзьями пользователей")
@RequestMapping("v1/api/friend")
@RestController
public class FriendController {

    @Autowired
    private FriendsService friendsService;

    @Operation(
            summary = "Создания дружбы",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    )
    @PostMapping("/{nickname}")
    public ResponseEntity<String> createFriend(@PathVariable String nickname, Principal principal) throws ValidationErrorWithMethod {
        friendsService.createFriend(principal.getName(), nickname);
        return  ResponseEntity.ok("Пользователь успешно добавлен в друзья");
    }
    @Operation(
            summary = "Удаление дружбы",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    )
    @DeleteMapping("/{nickname}")
    public ResponseEntity<String> deleteFriend(@PathVariable String nickname, Principal principal){
        friendsService.deleteFriend(principal.getName(), nickname);
        return  ResponseEntity.ok("Пользователь успешно удален из друзей");
    }
    @Operation(
            summary = "Получение всех друзей пользователя",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListResponseFriendDTO.class)))
    )
    @GetMapping("/{nickname}")
    public ListResponseFriendDTO getAllFriend(@PathVariable String  nickname){
        return friendsService.getAllFriend(nickname);
    }
}
