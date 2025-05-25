package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Post.*;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.PostUsersAppService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name="Управление постами пользователей")
@RestController
@RequestMapping("v1/api/user/post")
public class PostUsersController {

        @Autowired
        private PostUsersAppService postUsersAppService;

    // <------------------------ GET ЗАПРОСЫ -------------------------->

    @GetMapping("/{nickname}/{page}")
    @Operation(summary = "Получение постов по nickname пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseListPostDTOReaction.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponseListPostDTOReaction getPostByNicknameUsersApp(@PathVariable String nickname, @PathVariable int page, Principal principal) throws ValidationErrorWithMethod {
        return postUsersAppService.getPostsByNicknameReaction(nickname, page, principal.getName());
    }

    @GetMapping
    @Operation(summary = "Получение постов пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseListPostDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponseListPostDTO getPostByNicknameUsersApp(Principal principal){
        return postUsersAppService.getPostsForMe(principal.getName());
    }

    @GetMapping("/name")
    @Operation(summary = "Получение поста пользователя по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponsePostDTOReaction.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponsePostDTOReaction getPostByName(@RequestParam String namePost, Principal principal){
        return postUsersAppService.getPostWithReaction(namePost, principal.getName());
    }

    @GetMapping(value = "/file/{nameFile}", produces = {MediaType.IMAGE_PNG_VALUE, "video/mp4"})
    @Operation(summary = "Получение файла поста",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseListPostDTO.class))),
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
            @Valid @RequestPart("metadata") RequestPostDTO requestPostDTO, BindingResult result,
            @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!")MultipartFile[] multipartFiles, Principal principal
    ) throws ValidationErrorWithMethod, IOException {
        postUsersAppService.createPostUsersApp(requestPostDTO,principal.getName(), result, multipartFiles);
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
    public ResponseEntity<String> setUsers(@Valid @RequestPart("metadata") SetPostDTO setPostDTO, BindingResult result, @RequestPart(value = "file", required = false) @Schema(description = "Формат только png или mp4!") MultipartFile[] file, Principal principal) throws ValidationErrorWithMethod, IOException {
        postUsersAppService.setPostUserApp(setPostDTO,principal.getName(), result, file);
        return ResponseEntity.ok("Сущность поста пользователя изменена");
    }


    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @PreAuthorize("(#nickname == null) or hasAnyRole('ROLE_MANAGER')")
    @DeleteMapping({"/{namePost}", "/{nickname}/{namePost}"})
    @Operation(
            summary = "Удаление поста пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пост удален "),
                    @ApiResponse(responseCode = "404", description = "Пост не найден")
            }
    )
    public ResponseEntity<String> deletePost(@PathVariable(required = false) String nickname, @PathVariable String namePost, Principal principal) throws IOException {
        nickname = nickname == null? principal.getName(): nickname;
        postUsersAppService.deletePostUsersApp(namePost, nickname);
        return ResponseEntity.ok("Пост удален");
    }
}
