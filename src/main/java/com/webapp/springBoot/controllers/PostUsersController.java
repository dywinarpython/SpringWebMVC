package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.UsersPost.RequestUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.ResponceListUsersPostDTO;
import com.webapp.springBoot.DTO.UsersPost.SetUsersPostDTO;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.PostUsersAppService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name="Управление постами пользователей")
@RestController
@RequestMapping("api/user/post")
public class PostUsersController {

        @Autowired
        private PostUsersAppService postUsersAppService;

    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping("/{nickname}")
    @Operation(summary = "Получение постов по nickname пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponceListUsersPostDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponceListUsersPostDTO getPostByNicknameUsersApp(@PathVariable String nickname){
        return postUsersAppService.getPostsByNickname(nickname);
    }

    @GetMapping(value = "/file/{nameFile}", produces = {MediaType.IMAGE_PNG_VALUE, "video/mp4"})
    @Operation(summary = "Получение файла поста",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponceListUsersPostDTO.class))),
                    @ApiResponse(responseCode = "404", description = "ФАйл не найден")
            }
    )
    public ResponseEntity<Resource> getFilePost(@PathVariable String nameFile) throws IOException {
        return postUsersAppService.getFilePost(nameFile);
    }

    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Создание поста пользователя",
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
            @Valid @RequestPart("metadata") RequestUsersPostDTO requestUsersPostDTO, BindingResult result,
            @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!")MultipartFile[] multipartFiles
    ) throws ValidationErrorWithMethod, IOException {
        postUsersAppService.createPostUsersApp(requestUsersPostDTO, result, multipartFiles);
        return new ResponseEntity<>("Пост добавлен", HttpStatus.CREATED);
    }


    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение сущности поста пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(contentType = MediaType.APPLICATION_JSON_VALUE, name = "metadata")))
    )
    public ResponseEntity<String> setUsers(@Valid @RequestPart("metadata") SetUsersPostDTO setUsersPostDTO, BindingResult result, @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!") MultipartFile[] file) throws ValidationErrorWithMethod, IOException {
        postUsersAppService.setPostUserApp(setUsersPostDTO,result, file);
        return ResponseEntity.ok("Сущность поста пользователя изменена");
    }


    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @DeleteMapping("/{namePost}")
    @Operation(
            summary = "Удаление поста пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пост удален "),
                    @ApiResponse(responseCode = "404", description = "Пост не найден")
            }
    )
    public ResponseEntity<String> deletePost(@PathVariable String namePost) throws IOException {
        postUsersAppService.deletePostUsersApp(namePost);
        return ResponseEntity.ok("Пост удален");
    }
}
