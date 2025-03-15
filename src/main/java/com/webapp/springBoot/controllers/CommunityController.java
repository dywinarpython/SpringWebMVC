package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Community.*;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.CommunityService;
import com.webapp.springBoot.service.ImageCommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name="Управление сообществами")
@RestController
@RequestMapping("api/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;
    @Autowired
    private ImageCommunityService imageCommunityService;


    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping("/all")
    @Operation(
            summary="Вывод всех сообществ",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListCommunityDTO.class)))
            }
    )
    public ListCommunityDTO getAllCommunity(){
        return new ListCommunityDTO(communityService.getAllCommunity());
    }

    @GetMapping(value = "/image/{nameImage}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            summary="Вывод изображения сообщества",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = byte[].class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public byte[] getImageCommunity(@PathVariable String nameImage) throws IOException {
        return imageCommunityService.getImagePath(nameImage);
    }

    @GetMapping(value = "/name")
    @Operation(
            summary="Получение сообществ по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListCommunityDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public ListCommunityDTO getCommunityByName(@RequestParam String name){
        return new ListCommunityDTO(communityService.findByNameLike(name));
    }
    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @Operation(
            summary = "Добавление нового сообщества при привязке к пользователя по nickname",
            responses = @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PostMapping("add")
    public ResponseEntity<String> addNewCommunity(@Valid @RequestBody CommunityRequestDTO communityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.addNewCommunity(communityDTO,result);
        return new ResponseEntity<>("Сообщество добавлено", HttpStatus.CREATED);
    }

    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @Operation(
            summary = "Изменение nickname сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("nickname")
    public ResponseEntity<String> setNicknameCommunity(@Valid @RequestBody SetNicknameCommunityDTO setNicknameCommunity, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setNicknameCommunity(setNicknameCommunity,result);
        return ResponseEntity.ok("Nickname сообещства изменен");
    }

    @Operation(
            summary = "Изменение description сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("description")
    public ResponseEntity<String> setDescriptionCommunity(@Valid @RequestBody SetDescriptionCommunityDTO setDescriptionCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setDescriptionCommunity(setDescriptionCommunityDTO,result);
        return ResponseEntity.ok("Description сообещства изменен");
    }

    @Operation(
            summary = "Изменение name сообщества",
            responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    )
    @PatchMapping("name")
    public ResponseEntity<String> setNameCommunity(@Valid @RequestBody SetNameCommunityDTO setNameCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        communityService.setNameCommunity(setNameCommunityDTO,result);
        return ResponseEntity.ok("Name сообещства изменен");
    }


    @Operation(
            summary = "Изменение картинки сообщества",

            responses = {
                    @ApiResponse(responseCode = "200", description = "Изображение успешно обновлено"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации файла")
            }
    )
    @PatchMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> setImageCommunity(@RequestParam @Parameter(description = "Изображение только формата: PNG" ) MultipartFile file, @RequestParam String nickname) throws IOException, ValidationErrorWithMethod {
        communityService.setImageCommunity(file, nickname);
        return ResponseEntity.ok("Image сообещства изменен");
    }
    // <------------------------ DELETE ЗАПРОСЫ -------------------------->

    @DeleteMapping("/{nickname}")
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

    @DeleteMapping("image/{nickname}")
    @Operation(
            summary = "Удаление изображения сообщества по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> deleteImageCommunityByNickname(@PathVariable String nickname) throws IOException {
        communityService.deleteImageCommunity(nickname);
        return ResponseEntity.ok("Изображение сообщества удалено");
    }

}
