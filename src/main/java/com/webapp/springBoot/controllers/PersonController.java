package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.APiResponceUserDOTO;
import com.webapp.springBoot.DTO.ApiREsponceDeleteIDDOTO;
import com.webapp.springBoot.DTO.ApiResponceDocsDOTO;
import com.webapp.springBoot.DTO.ApiResponceListUsersDOTO;
import com.webapp.springBoot.entity.Users;
import com.webapp.springBoot.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PersonController {
    @Autowired
    private UsersService usersService;
    @Operation(
        summary = "Проверка подключения к Api",
        responses = {
                @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceDocsDOTO.class)))
        }
    )
    @GetMapping("/check")
    public Map<String, String> checkAPIConnect(){
        return Map.of("messages", "The API is working correct");
    }



    @PostMapping("/add")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = APiResponceUserDOTO.class)))
            },
            parameters = @Parameter(name="users", description = "информация о пользователе", required = true, schema = @Schema(implementation = APiResponceUserDOTO.class))
    )
    public Users saveNewUser(@RequestBody Users users){
        usersService.saveUser(users);
        return users;
    }





    @PostMapping("/findByName")
    @Operation(
            summary="Поиск пользователя по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDOTO.class)))
            },
            parameters = @Parameter(name="Имя", description = "Поиск по имени пользователя", required = true, schema = @Schema(implementation = ApiResponceDocsDOTO.class))
    )
    public List<Users> findUserByName(@RequestParam String name){
        return usersService.getUserByName(name);
    }




    @PostMapping("/findAll")
    @Operation(
            summary="Вывод всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDOTO.class)))
            }
    )
    public List<Users> findAllUser(){
        return usersService.getAllUser();
    }


    @DeleteMapping("/deleteById")
    @Operation(
            summary="Удаления пользователя по ID",
            responses = {
                    @ApiResponse(responseCode = "202", content = @Content(schema = @Schema(implementation = ApiResponceDocsDOTO.class)))
            },
            parameters = @Parameter(name="id", description = "ID пользователя", required = true, schema = @Schema(implementation = ApiREsponceDeleteIDDOTO.class))
    )
    public ApiResponceDocsDOTO deleteUserByID(@RequestParam Long id) throws Exception {
        String stringResponce = String.format("Пользователь с id=%d удален его имя было: %s", id, usersService.deleteUserByID(id).toString());
        return new ApiResponceDocsDOTO(stringResponce);
    }
}
