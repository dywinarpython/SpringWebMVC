package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Community.*;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.CommunityService;
import com.webapp.springBoot.service.ImageCommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
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
import java.security.Principal;

@Tag(name="Управление сообществами")
@RestController
@RequestMapping("api/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;
    @Autowired
    private ImageCommunityService imageCommunityService;


    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping
    @Operation(
            summary="Вывод сообществ по 10 штук",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListCommunityDTO.class)))
            }
    )
    public ListCommunityDTO getCommunity(@RequestParam(value = "page", defaultValue = "0", required = false) int page){
        return new ListCommunityDTO(communityService.getСommunity(page));
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
    public ListCommunityDTO getCommunityByName(@RequestParam String name, @RequestParam(value = "page", defaultValue = "0", required = false) int page){
        return new ListCommunityDTO(communityService.findByNameLike(name, page));
    }

    @GetMapping(value = "/{nickname}")
    @Operation(
            summary="Получение сообществ по nickname",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CommunityResponseDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public CommunityResponseDTO getCommunityByNickname(@PathVariable String nickname){
        return communityService.getByNickname(nickname);
    }
    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @PostMapping
    @Operation(
            summary = "Добавление нового сообщества при привязке к пользователя по nickname",
            responses = {@ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> addNewCommunity(@Valid @RequestBody CommunityRequestDTO communityDTO, BindingResult result, Principal principal) throws ValidationErrorWithMethod {
        communityService.addNewCommunity(communityDTO,principal.getName(), result);
        return new ResponseEntity<>("Сообщество добавлено", HttpStatus.CREATED);
    }

    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение сущности Community",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(contentType = MediaType.APPLICATION_JSON_VALUE, name = "metadata")))
    )
    public ResponseEntity<String> setNicknameCommunity(@Valid @RequestPart("metadata") SetCommunityDTO setCommunityDTO, BindingResult result, @RequestPart(value = "image", required = false) @Schema(description = "Формат только png!") MultipartFile file, Principal principal) throws ValidationErrorWithMethod, IOException {
        communityService.setCommunity(setCommunityDTO, principal.getName(), result,  file);
        return ResponseEntity.ok("Сущность сообещства изменена");
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
    public ResponseEntity<String> deleteCommunityByNickname(@PathVariable String nickname, Principal principal) throws IOException{
        communityService.deleteCommunityByNickname(nickname, principal.getName());
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
    public ResponseEntity<String> deleteImageCommunityByNickname(@PathVariable String nickname, Principal principal) throws IOException {
        communityService.deleteImageCommunity(nickname, principal.getName());
        return ResponseEntity.ok("Изображение сообщества удалено");
    }

}
