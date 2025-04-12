package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Meneger.BanUsersDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.BanUsersAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Secured(value = {"ROLE_ADMIN", "ROLE_MENEGER"})
@Tag(name = "Управление доступом пользователей")
@RestController
@RequestMapping("v1/api/meneger")
public class MenegerController {

    @Autowired
    private BanUsersAppService banUsersAppService;

    @Operation(
            description = "Бан пользователей, ВРЕМЯ В МИЛИСЕКУНДАХ!!!!!!!!",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    @PostMapping("/ban")
    public ResponseEntity<String> banUsersByNickname(@RequestBody BanUsersDTO banUsersDTO, BindingResult bindingResult) throws ValidationErrorWithMethod {
        banUsersAppService.setBanUsers(banUsersDTO, bindingResult);
        return ResponseEntity.ok("Права пользователя изменены");
    }
}
