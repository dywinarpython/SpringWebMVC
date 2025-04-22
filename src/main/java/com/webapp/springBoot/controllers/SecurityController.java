package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Users.LoginDto;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.security.JWTConfig.Tokens;
import com.webapp.springBoot.DTO.OAuth2.OAuth2RecordDTO;
import com.webapp.springBoot.security.service.TokenUser;
import com.webapp.springBoot.util.DeleteCookie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

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
    public  void loginOAuth2(){

        }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest reguest, HttpServletResponse response) throws ValidationErrorWithMethod {
        DeleteCookie.deleteCookie(response, "__Host_authinticatedToken");
        return ResponseEntity.ok(Map.of("message", "Успешный logout"));
    }
}
