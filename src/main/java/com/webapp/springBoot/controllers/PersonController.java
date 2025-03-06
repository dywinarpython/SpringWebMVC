package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Person.APiResponceUserDTO;
import com.webapp.springBoot.DTO.ApiResponceDocsDTO;
import com.webapp.springBoot.DTO.Person.ApiResponceListUsersDTO;
import com.webapp.springBoot.entity.Users;
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



    @PostMapping("/add")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> saveNewUser(@RequestBody @Valid APiResponceUserDTO users, BindingResult result) throws ValidationErrorWithMethod {
        usersService.saveUser(users, result);
        return ResponseEntity.ok("Пользователь добавлен");
    }


    @PostMapping("/findByName")
    @Operation(
            summary="Поиск пользователя по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))

            })
    public List<Users> findUserByName(@RequestParam String name){
        return usersService.getUserByName(name);
    }

    @GetMapping("/findAll")
    @Operation(
            summary="Вывод всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDTO.class)))
            }
    )
    public List<Users> findAllUser(){
        return usersService.getAllUser();
    }


    @DeleteMapping("/deleteById")
    @Operation(
            summary="Удаления пользователя по ID",
            responses = {
                    @ApiResponse(responseCode = "202", content = @Content(schema = @Schema(implementation = ApiResponceDocsDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            },
            parameters = @Parameter(name="id", description = "ID пользователя", required = true)
    )
    public ResponseEntity<String> deleteUserByID(@RequestParam Long id) {
            usersService.deleteUserByID(id);
            return ResponseEntity.ok("Пользователь был успешно удален");
    }

    @GetMapping("/ageBetween")
    @Operation(
            summary = "Получение пользователе по возрасту в определенном промежутке",
            responses = @ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDTO.class))
            ),
            parameters = {
                    @Parameter(description = "Значение возраста от (включительно)", name = "ageOne", required = true),
                    @Parameter(description = "Значение возраста до (включительно)", name="ageTwo", required = true)
            })
    public List<Users> getUsersBetweenAge(@RequestParam int ageOne, @RequestParam  int ageTwo ){
        return usersService.getAgeUserBetween(ageOne, ageTwo);
        }

    @GetMapping("/findByNameAndSurname")
    @Operation(
            summary = "Получение пользователе по возрасту и имени",
            responses = @ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponceListUsersDTO.class))
            ),
            parameters = {
                    @Parameter(description = "Имя пользователя", name = "name", required = true),
                    @Parameter(description = "Фамилия пользователя", name="surname", required = true)
            }
    )
    public List<Users> findUsersByNameAndSurname(@RequestParam String name, @RequestParam String surname){
        return usersService.findByNameAndSurname(name, surname);
    }
    }

