package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Community.*;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<String> addNewCommunity(@Valid @RequestBody CommunityDTO communityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.addNewCommunity(communityDTO,result);
        return new ResponseEntity<>("Сообщество добавлено", HttpStatus.CREATED);
    }

    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @Operation(
            summary = "Изменение nickname сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("setNickname")
    public ResponseEntity<String> setNicknameCommunity(@Valid @RequestBody SetNicknameCommunityDTO setNicknameCommunity, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setNicknameCommunity(setNicknameCommunity,result);
        return ResponseEntity.ok("Nickname сообещства изменен");
    }

    @Operation(
            summary = "Изменение description сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("setDescription")
    public ResponseEntity<String> setDescriptionCommunity(@Valid @RequestBody SetDescriptionCommunityDTO setDescriptionCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setDescriptionCommunity(setDescriptionCommunityDTO,result);
        return ResponseEntity.ok("Description сообещства изменен");
    }


    @Operation(
            summary = "Изменение name сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("setName")
    public ResponseEntity<String> setNameCommunity(@Valid @RequestBody SetNameCommunityDTO setNameCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setNameCommunity(setNameCommunityDTO,result);
        return ResponseEntity.ok("Name сообещства изменен");
    }
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
