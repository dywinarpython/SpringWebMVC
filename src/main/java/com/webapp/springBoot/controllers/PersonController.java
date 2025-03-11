package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Person.UserDTO;
import com.webapp.springBoot.DTO.Person.ListUsersDTO;
import com.webapp.springBoot.DTO.Person.SetNicknameDTO;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class PersonController {
    @Autowired
    private UsersService usersService;

    // <------------------------ GET ЗАПРОСЫ -------------------------->
    @GetMapping("/findByName")
    @Operation(
            summary="Поиск пользователя по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))

            })
    public List<UsersApp> findUserByName(@RequestParam String name){
        return usersService.getUserByName(name);
    }

    @GetMapping("/findAll")
    @Operation(
            summary="Вывод всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class)))
            }
    )
    public ListUsersDTO getAllUser(){
        return new ListUsersDTO(usersService.getAllUser());
    }




    @GetMapping("/ageBetween")
    @Operation(
            summary = "Получение пользователе по возрасту в определенном промежутке",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            parameters = {
                    @Parameter(description = "Значение возраста от (включительно)", name = "ageOne", required = true),
                    @Parameter(description = "Значение возраста до (включительно)", name="ageTwo", required = true)
            })
    public List<UsersApp> getUsersBetweenAge(@RequestParam int ageOne, @RequestParam  int ageTwo ){
        return usersService.getAgeUserBetween(ageOne, ageTwo);
        }

    @GetMapping("/findByNameAndSurname")
    @Operation(
            summary = "Получение пользователе по возрасту и имени",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            parameters = {
                    @Parameter(description = "Имя пользователя", name = "name", required = true),
                    @Parameter(description = "Фамилия пользователя", name="surname", required = true)
            }
    )
    public List<UsersApp> findUsersByNameAndSurname(@RequestParam String name, @RequestParam String surname){
        return usersService.findByNameAndSurname(name, surname);
    }


    @GetMapping("/findByNickname/{nickname}")
    @Operation(
            summary = "Получение пользователе по nickname",
            responses = {@ApiResponse(
                                responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                         @ApiResponse(
                                responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            parameters = {
                    @Parameter(description = "Nickname пользователя", name = "nickname", required = true)
            }
    )
    public UsersApp findByNickname(@PathVariable String nickname){
        return usersService.findByNickname(nickname);
    }


    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @PostMapping("/add")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> saveNewUser(@Valid @RequestBody UserDTO users, BindingResult result) throws ValidationErrorWithMethod {
        usersService.saveUser(users, result);
        return ResponseEntity.ok("Пользователь добавлен");
    }



    // <------------------------ PATCH ЗАПРОСЫ -------------------------->

    @PatchMapping("/setNickname")
    @Operation(
            summary = "Изменение nickname пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setNickname(@Valid @RequestBody SetNicknameDTO apiResponceSetNicknameDTO, BindingResult result){
        usersService.setNickname(apiResponceSetNicknameDTO, result);
        return ResponseEntity.ok("Nickname изменен");
    }


    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @DeleteMapping("/deleteById/{nickname}")
    @Operation(
            summary="Удаления пользователя по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    public ResponseEntity<String> deleteUserByNickname(@PathVariable String nickname) {
        usersService.deleteUserByNickname(nickname);
        return ResponseEntity.ok("Пользователь был успешно удален");
    }
    }







