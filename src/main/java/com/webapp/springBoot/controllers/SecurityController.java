package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Users.LoginDto;
import com.webapp.springBoot.security.JWTConfig.Tokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RequestMapping("/api/security")
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

}
