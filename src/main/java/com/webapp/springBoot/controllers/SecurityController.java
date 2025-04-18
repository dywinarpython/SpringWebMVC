package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Users.LoginDto;
import com.webapp.springBoot.security.JWTConfig.Tokens;
import com.webapp.springBoot.DTO.OAuth2.OAuth2RecordDTO;
import com.webapp.springBoot.security.service.TokenUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RequestMapping("v1/api/security")
@RestController
@Tag(name = "Безопасность")
public class SecurityController {
    @Operation(
            responses = {
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Tokens.class)))
            },
            description = "Вход"
    )
    @PostMapping("/login")
    public void login(@RequestBody LoginDto loginDto){

    }

    @Operation(
            responses = {
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Tokens.class)))
            },
            description = "Обновление access токена!"
    )
    @PostMapping("/refresh")
    public void refresh(){

    }


    @GetMapping("/checkLog")
    public  ResponseEntity<Map<String, String>> checkLogin(Principal principal){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Вход успешен, вошел пользователь: %s".formatted(principal.getName())));
    }

    @Operation(
            responses = {
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Tokens.class)))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = OAuth2RecordDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            description = "Вход по Oauth2"
    )
    @PostMapping("/oauth2/google/login")
    public  ResponseEntity<Map<String, String>> checkLoginOAuth2(@AuthenticationPrincipal TokenUser principal){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Вход успешен, вошел пользователь: %s".formatted(principal.getGoogleUserInfo().getName())));
        }




}
