package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.service.ImageUsersAppService;
import com.webapp.springBoot.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Tag(name="Управление пользователями")
@RestController
@RequestMapping("/api/user")
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private ImageUsersAppService imageUsersAppService;


    // <------------------------ GET ЗАПРОСЫ -------------------------->
    @GetMapping("/name")
    @Operation(
            summary="Поиск пользователя по имени",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))

            })
    public ListUsersDTO getUserByName(@RequestParam String name){
        return usersService.getUserByName(name);
    }

    @GetMapping("/all")
    @Operation(
            summary="Вывод всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class)))
            }
    )
    public ListUsersDTO getAllUser(){
        return usersService.getAllUser();
    }




    @GetMapping("/age")
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
    public ListUsersDTO getUsersBetweenAge(@RequestParam int ageOne, @RequestParam  int ageTwo ){
        return usersService.getAgeUserBetween(ageOne, ageTwo);
        }

    @GetMapping("/nameSurname")
    @Operation(
            summary = "Получение пользователе по фамилии и имени (ищет по первой букве именеи или фамилии или по первой буквы фамилиии. Пример: Иван Г -> Иван Горьков)",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = ListUsersDTO.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            parameters = {
                    @Parameter(description = "Имя пользователя", name = "name", required = true),
                    @Parameter(description = "Фамилия пользователя", name="surname", required = true)
            }
    )
    public ListUsersDTO getUsersByNameAndSurname(@RequestParam String name, @RequestParam String surname){
        return usersService.findByNameAndSurname(name, surname);
    }

    @GetMapping("/{nickname}")
    @Operation(
            summary = "Получение пользователе по nickname",
            responses = {@ApiResponse(
                                responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponceDTO.class))),
                         @ApiResponse(
                                responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            parameters = {
                    @Parameter(description = "Nickname пользователя", name = "nickname", required = true)
            }
    )
    public UserResponceDTO getByNickname(@PathVariable String nickname){
        return usersService.getUserByNickname(nickname);
    }

    @GetMapping("/communty/{nickname}")
    @Operation(
            summary="Получение всех сообществ пользователя, поиск по полю nickname",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ListCommunityUsersDTO.class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ListCommunityUsersDTO getCommunityForUserByNickname(@PathVariable String nickname){
        return usersService.getAllCommunityForUser(nickname);
    }

    @GetMapping(value = "/image/{nameImage}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            summary="Вывод изображения пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = byte[].class))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public byte[] getImageUsersApp(@PathVariable String nameImage) throws IOException {
        return imageUsersAppService.getImagePath(nameImage);
    }



    // <------------------------ POST ЗАПРОСЫ -------------------------->
    @PostMapping("/add")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> saveNewUser(@Valid @RequestBody UserRequestDTO users, BindingResult result) throws ValidationErrorWithMethod {
        usersService.saveUser(users, result);
        return new ResponseEntity<>("Пользователь добавлен", HttpStatus.CREATED);
    }



    // <------------------------ PATCH ЗАПРОСЫ -------------------------->

    @PatchMapping("/nickname")
    @Operation(
            summary = "Изменение nickname пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setNickname(@Valid @RequestBody SetNicknameDTO apiResponceSetNicknameDTO, BindingResult result) throws ValidationErrorWithMethod {
        usersService.setNickname(apiResponceSetNicknameDTO, result);
        return ResponseEntity.ok("Nickname изменен");
    }

    @PatchMapping("/name")
    @Operation(
            summary = "Изменение name пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setName(@Valid @RequestBody SetNameDTO setNameDTO, BindingResult result) throws ValidationErrorWithMethod {
        usersService.setName(setNameDTO, result);
        return ResponseEntity.ok("Name изменен");
    }

    @PatchMapping("/surname")
    @Operation(
            summary = "Изменение surname пользователя",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> setSurname(@Valid @RequestBody SetSurnameDTO setSurnameDTO, BindingResult result) throws ValidationErrorWithMethod {
        usersService.setSurname(setSurnameDTO, result);
        return ResponseEntity.ok("Surname изменен");
    }

    @Operation(
            summary = "Изменение изображения пользователя",

            responses = {
                    @ApiResponse(responseCode = "200", description = "Изображение успешно обновлено"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации файла")
            }
    )
    @PatchMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> setImagesUsersApp(@RequestPart("file") @Parameter(description = "Изображение только формата: PNG" ) MultipartFile file, @RequestParam String nickname) throws IOException, ValidationErrorWithMethod {
        usersService.setImageUsersApp(file, nickname);
        return ResponseEntity.ok("Image пользователя изменен");
    }

    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @DeleteMapping("/{nickname}")
    @Operation(
            summary="Удаления пользователя по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    public ResponseEntity<String> deleteUserByNickname(@PathVariable String nickname) throws IOException {
        usersService.deleteUserByNickname(nickname);
        return ResponseEntity.ok("Пользователь был успешно удален");
    }

    @DeleteMapping("image/{nickname}")
    @Operation(
            summary = "Удаление изображения пользователя по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> deleteImagesUsersApp(@PathVariable String nickname) throws IOException {
        usersService.deleteImageUsersApp(nickname);
        return ResponseEntity.ok("Изображение пользователя удалено");
    }
    }







