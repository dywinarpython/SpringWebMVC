package com.webapp.springBoot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Tag(name = "Главный контроллер")
@RestController
@RequestMapping("/api")
public class MainApiController{
    @Operation(
            summary = "Проверка подключения к Api",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> checkAPIConnect(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Подключения устаовленно, вошел пользователь %s".formatted(jwt.getClaim("given_name").toString())));
    }
}