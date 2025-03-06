package com.webapp.springBoot.controllers;

import com.webapp.springBoot.DTO.ApiResponceDocsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MainApiController{
    @Operation(
            summary = "Проверка подключения к Api",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceDocsDTO.class)))
            }
    )
    @GetMapping("/check")
    public ApiResponceDocsDTO checkAPIConnect(){
        return new ApiResponceDocsDTO("Подключение к API успешно");
    }
}