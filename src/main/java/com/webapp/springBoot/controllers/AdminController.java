package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Admin.AddNewRoleUsersAppDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.RolesService;
import com.webapp.springBoot.service.UsersService;
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

import java.io.IOException;


@Tag(name = "Администрирование пользователей")
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RolesService rolesService;

    @Operation(
            summary = "Добавление ролей пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    @PostMapping("/roles")
    public ResponseEntity<String> addNewRoles(@RequestParam String nameRole) throws ValidationErrorWithMethod {
        rolesService.addNewRoles(nameRole);
        return ResponseEntity.ok("Роль добавлена");
    }

    @PatchMapping("user/role")
    @Operation(
            summary = "Добавление прав пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setUsersAppRoles(@RequestBody AddNewRoleUsersAppDTO addNewRoleUsersAppDTO, BindingResult result) throws ValidationErrorWithMethod, IOException {
        usersService.addRolesUsersApp(addNewRoleUsersAppDTO, result);
        return ResponseEntity.ok("Права пользователя изменены");
    }

    @DeleteMapping("user/role")
    @Operation(
            summary = "Изменение прав пользователя, удаление прав",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setUsersAppRole(@RequestBody AddNewRoleUsersAppDTO addNewRoleUsersAppDTO, BindingResult result) throws ValidationErrorWithMethod, IOException {
        usersService.deleteRolesUsersApp(addNewRoleUsersAppDTO, result);
        return ResponseEntity.ok("Права пользователя изменены");
    }
}
