package com.webapp.springBoot.controllers;

import com.webapp.springBoot.DTO.UserReaction.RequestUserReactionDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.UserPostReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Управление реакциями пользователей")
@RestController
@RequestMapping("v1/api/user/reaction")
public class ReactionController {

    @Autowired
    private UserPostReactionService userPostReactionService;

    @PostMapping
    @Operation(
            summary = "Создание реакции пользователя на пост",
            description = "Логика такая: пользователь не может добавить реакцию на свой же пост",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            }
    )
    public ResponseEntity<String> createReaction(
            @Valid @RequestBody RequestUserReactionDTO requestUserReactionDTO,
            BindingResult bindingResult,
            Principal principal) throws ValidationErrorWithMethod {

        userPostReactionService.createUserReaction(requestUserReactionDTO, bindingResult, principal.getName());
        return ResponseEntity.status(201).body("Реакция создана");
    }


    @DeleteMapping("/{namePost}")
    @Operation(summary = "Удаление реакции пользователя по посту",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponseEntity<String> deleteReaction(@RequestParam String namePost, Principal principal) {
        userPostReactionService.deleteUserReaction(principal.getName(),namePost);
        return ResponseEntity.status(200).body("Реакция удалена");
    }

    @PatchMapping
    @Operation(summary = "Изменение реакции пользователя по посту",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    public ResponseEntity<String> deleteReaction( @Valid @RequestBody RequestUserReactionDTO requestUserReactionDTO,
                                                 BindingResult bindingResult,
                                                 Principal principal) throws ValidationErrorWithMethod {
        userPostReactionService.updateUserReaction(requestUserReactionDTO, bindingResult, principal.getName());
        return ResponseEntity.status(200).body("Реакция изменена");
    }
}
