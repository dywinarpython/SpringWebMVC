package com.webapp.springBoot.controllers;


import com.webapp.springBoot.DTO.OAuth2.UserRequestOAuth2DTO;
import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.service.ImageUsersAppService;
import com.webapp.springBoot.service.UsersService;
import com.webapp.springBoot.util.DeleteCookie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;


@Tag(name="Управление пользователями")
@RestController
@RequestMapping("v1/api/user")
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
    public ListUsersDTO getUserByName(@RequestParam String name, @RequestParam(value = "page", defaultValue = "0", required = false) int page){
        return usersService.getUserByName(name, page);
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
    public ListUsersDTO getUsersBetweenAge(@RequestParam int ageOne, @RequestParam  int ageTwo, @RequestParam(value = "page", defaultValue = "0", required = false) int page ){
        return usersService.getAgeUserBetween(ageOne, ageTwo, page);
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
    public ListUsersDTO getUsersByNameAndSurname(@RequestParam String name, @RequestParam String surname, @RequestParam(value = "page", defaultValue = "0", required = false) int page){
        return usersService.findByNameAndSurname(name, surname, page);
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
    @PostMapping("/registr")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> saveNewUser(@Valid @RequestBody UserRequestDTO users, BindingResult result, HttpServletResponse response) throws Exception {
        usersService.saveUserInCache(users, result, response);
        return new ResponseEntity<>("Пользователь добавлен в кеш", HttpStatus.CREATED);
    }
    @PostMapping("/check")
    @Operation(
            summary="Проверка кода",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> checkNewUser(@Valid @RequestBody VerifyNumberDTO verifyNumberDTO, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws ValidationErrorWithMethod {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new ValidationErrorWithMethod("Не переданны необходимые куки!");
        }
        Cookie cookie = Arrays.stream(request.getCookies()).filter(cookieFilter -> Objects.equals(cookieFilter.getName(), "VERIFY_PHONE")).findFirst().orElseThrow(() -> new ValidationErrorWithMethod("Не переданны необходимые куки!"));
        String uuid = cookie.getValue();
        usersService.saveUser(verifyNumberDTO, uuid, result);
        DeleteCookie.deleteCookie(response, cookie.getName());
        return new ResponseEntity<>("Пользователь добавлен", HttpStatus.CREATED);
    }

    @PostMapping("/registr/oauth2")
    @Operation(
            summary="Добавление нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<String> saveNewUserOuAth2(@Valid @RequestBody UserRequestOAuth2DTO userRequestOAuth2DTO, BindingResult result, HttpServletResponse response, HttpServletRequest request) throws ValidationErrorWithMethod {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new ValidationErrorWithMethod("Не переданны необходимые куки!");
        }
        Cookie cookie = Arrays.stream(request.getCookies()).filter(cookieFilter -> Objects.equals(cookieFilter.getName(), "REG_DRAFT_ID")).findFirst().orElseThrow(() -> new ValidationErrorWithMethod("Не переданны необходимые куки!"));
        String uuid = cookie.getValue();
        usersService.saveUser(userRequestOAuth2DTO,uuid, result);
        DeleteCookie.deleteCookie(response, cookie.getName());
        return new ResponseEntity<>("Пользователь добавлен", HttpStatus.CREATED);
    }



    // <------------------------ PATCH ЗАПРОСЫ -------------------------->
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение сущности пользователи",
            responses = {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(contentType = MediaType.APPLICATION_JSON_VALUE, name = "metadata")))
    )
    public ResponseEntity<String> setUsers(@Valid @RequestPart("metadata") SetUserDTO setUserDTO, BindingResult result, @RequestPart(value = "image", required = false) @Schema(description = "Формат только png!") MultipartFile file, Principal principal) throws ValidationErrorWithMethod, IOException {
        usersService.setUsers(setUserDTO, principal.getName(),result, file);
        return ResponseEntity.ok("Сущность пользователя изменена");
    }



    // <------------------------ DELETE ЗАПРОСЫ -------------------------->
    @DeleteMapping
    @Operation(
            summary="Удаления пользователя по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> deleteUserByNickname(Principal principal) throws IOException {
        usersService.deleteUserByNickname(principal.getName());
        return ResponseEntity.ok("Пользователь был успешно удален");
    }

    @DeleteMapping({"/image", "/image/{nickname}"})
    @PreAuthorize("(#nickname == null) or hasAnyRole('ROLE_MANAGER')")
    @Operation(
            summary = "Удаление изображения пользователя по nickname",
            responses =  {@ApiResponse(
                    responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(
                            responseCode = "404", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public ResponseEntity<String> deleteImagesUsersApp(@PathVariable(required = false) String nickname, Principal principal) throws IOException {
        nickname = nickname == null? principal.getName() : nickname;
        usersService.deleteImageUsersApp(nickname);
        return ResponseEntity.ok("Изображение пользователя удалено");
    }


    }







