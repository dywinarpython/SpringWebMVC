package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.CommunityPost.RequestCommunityPostDTO;
import com.webapp.springBoot.DTO.CommunityPost.ResponceListCommunityPostDTO;
import com.webapp.springBoot.DTO.CommunityPost.SetCommunityPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceListUsersPostDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.PostCommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;


@Tag(name="Управление постами сообществ")
@RestController
@RequestMapping("v1/api/community/post")
public class PostCommunityController {

        @Autowired
        private PostCommunityService postCommunityService;

    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping("/{nickname}")
    @Operation(summary = "Получение постов по nickname сообщества",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponceListUsersPostDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponceListCommunityPostDTO getPostByNicknameCommunityPosts(@PathVariable String nickname){
        return postCommunityService.getPostsByNickname(nickname);
    }

    @GetMapping(value = "/file/{nameFile}", produces = {MediaType.IMAGE_PNG_VALUE, "video/mp4"})
    @Operation(summary = "Получение файла поста",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponceListUsersPostDTO.class))),
                    @ApiResponse(responseCode = "404", description = "ФАйл не найден")
            }
    )
    public ResponseEntity<Resource> getFilePost(@PathVariable String nameFile) throws IOException {
        return postCommunityService.getFilePost(nameFile);
    }

    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Создание поста сообщества",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пост создан"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = {
                                    @Encoding(
                                            name = "metadata",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<String> addNewPost(
            @Valid @RequestPart("metadata") RequestCommunityPostDTO requestCommunityPostDTO, BindingResult result,
            @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!")MultipartFile[] multipartFiles, Principal principal
    ) throws ValidationErrorWithMethod, IOException {
        postCommunityService.createPostCommunity(requestCommunityPostDTO, principal.getName(), result, multipartFiles);
        return new ResponseEntity<>("Пост добавлен", HttpStatus.CREATED);
    }


    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение сущности поста сообщества",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(contentType = MediaType.APPLICATION_JSON_VALUE, name = "metadata")))
    )
    public ResponseEntity<String> setCommunityPosts(@Valid @RequestPart("metadata") SetCommunityPostDTO setCommunityPostDTO, BindingResult result, @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!") MultipartFile[] file, Principal principal) throws ValidationErrorWithMethod, IOException {
        postCommunityService.setPostCommunnity(setCommunityPostDTO, principal.getName(), result, file);
        return ResponseEntity.ok("Сущность поста сообщества изменена");
    }


    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @DeleteMapping("/{namePost}")
    @Operation(
            summary = "Удаление поста сообщества",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пост удален "),
                    @ApiResponse(responseCode = "404", description = "Пост не найден")
            }
    )
    public ResponseEntity<String> deletePost(@PathVariable String namePost, Principal principal) throws IOException {
        postCommunityService.deletePostCommunity(namePost, principal.getName());
        return ResponseEntity.ok("Пост удален");
    }
}
