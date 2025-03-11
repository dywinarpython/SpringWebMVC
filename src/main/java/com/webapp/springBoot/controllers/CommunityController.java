package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Community.CommunityDTO;
import com.webapp.springBoot.DTO.Community.ListCommunityDTO;
import com.webapp.springBoot.DTO.Person.ListUsersDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("api/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;



    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping("/findAll")
    @Operation(
            summary="Вывод всех сообществ",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListCommunityDTO.class)))
            }
    )
    public ListCommunityDTO getAllCommunity(){
        return new ListCommunityDTO(communityService.getAllCommunity());
    }

    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @Operation(
            summary = "Добавление нового сообщества при привязке к пользователя по nickname",
            responses = @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PostMapping("add")
    public ResponseEntity<String> addNewCommunity(@Valid @RequestBody CommunityDTO communityDTO, BindingResult result) throws IOException, ValidationErrorWithMethod {
        communityService.addNewCommunity(communityDTO,result);
        return ResponseEntity.ok("Сообщество добавлено");
    }
    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    // <------------------------ DELETE ЗАПРОСЫ -------------------------->

    @DeleteMapping("deleteByNickname/{nickname}")
    @Operation(
            summary = "Удаление сообщество по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> deleteCommunityByNickname(@PathVariable String nickname){
        communityService.deleteCommunityByNickname(nickname);
        return ResponseEntity.ok("Сообщество удалено");
    }

}
