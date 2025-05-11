package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Admin.AddNewRoleUsersAppDTO;
import com.webapp.springBoot.DTO.Community.CommunityResponseDTO;
import com.webapp.springBoot.DTO.OAuth2.UserRequestOAuth2DTO;
import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.cache.DeleteCacheService;
import com.webapp.springBoot.entity.*;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.RolesRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import com.webapp.springBoot.security.SecurityUsersService;
import com.webapp.springBoot.cache.CacheSaveVerifyRecord;
import com.webapp.springBoot.util.VerifyPhoneService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsersService {
    @Autowired
    private UsersAppRepository userRepository;
    @Autowired
    private ImageUsersAppService imageUsersAppService;
    @Autowired
    private ImageCommunityService imageCommunityService;
    @Autowired
    private SecurityUsersService securityUsersService;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private FilePostsUsersAppService filePostsUsersAppService;
    @Autowired
    private VerifyPhoneService verifyPhone;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private DeleteCacheService deleteCacheService;


    // <-----------------------Сохранения сущности пользователя в кеш-------------------->
    public void saveUserInCache(UserRequestDTO userRequestDTO, BindingResult result, HttpServletResponse response) throws Exception {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        if(userRepository.findByPhoneNumber(userRequestDTO.getPhone()).isPresent()){
            throw new ValidationErrorWithMethod("На один аккаунт один номер телефона");
        }
        verifyPhone.sendConfirmationCode(userRequestDTO.getPhone(), userRequestDTO , response);
    }
    // <-----------------------Сохранения сущности пользователя в  UsersApp-------------------->
    @CachePut(value = "USER_RESPONSE", key = "#result.getNickname()")
    public UserResponceDTO saveUser(VerifyNumberDTO verifyNumberDTO, String uuid, BindingResult bindingResult) throws ValidationErrorWithMethod {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        Cache cache = cacheManager.getCache("VERIFY_PHONE");
        if(cache == null){
            throw new ValidationErrorWithMethod("Сессия регистрации истекла или недействительна.");
        }
        CacheSaveVerifyRecord cacheSaveVerifyRecord = cache.get(uuid, CacheSaveVerifyRecord.class);
        if(cacheSaveVerifyRecord == null){
            throw new ValidationErrorWithMethod("Сессия регистрации истекла или недействительна.");
        }
        if(!Objects.equals(cacheSaveVerifyRecord.code(), verifyNumberDTO.getCode())){
            throw new ValidationErrorWithMethod("Код не совпадает!");
        }
        UserRequestDTO userRequestDTO = cacheSaveVerifyRecord.userRequestDTO();
        Roles roles = rolesService.getRolesByName("USER");
        UsersApp usersApp = new UsersApp();
        usersApp.setName(userRequestDTO.getName());
        usersApp.setSurname(userRequestDTO.getSurname());
        usersApp.setAge(userRequestDTO.getAge());
        usersApp.setNickname(userRequestDTO.getNickname());
        usersApp.setPassword(securityUsersService.passwordEncode(userRequestDTO.getPassword()));
        usersApp.rolesAdd(roles);
        usersApp.setPhoneNumber(userRequestDTO.getPhone());
        cache.evict(uuid);
        return new UserResponceDTO(userRepository.save(usersApp), imageUsersAppService.getImageName(usersApp));
        }
    @CachePut(value = "USER_RESPONSE", key = "#result.getNickname()")
    public UserResponceDTO saveUser(UserRequestOAuth2DTO userRequestOAuth2DTO, String uuid, BindingResult bindingResult) throws ValidationErrorWithMethod {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        UsersApp usersApp = new UsersApp();
        Cache cache = cacheManager.getCache("REGISTER_OAUTH2");
        if (cache == null) {
            throw new IllegalStateException("Сессия регистрации истекла или недействительна.");
        }

        GoogleUserInfo googleUserInfo = cache.get(uuid, GoogleUserInfo.class);
        if (googleUserInfo == null) {
            throw new ValidationErrorWithMethod("Сессия регистрации истекла или недействительна.");
        }
        if(userRequestOAuth2DTO.getName() == null && googleUserInfo.getName() !=null){
            usersApp.setName(googleUserInfo.getName());
        } else if(userRequestOAuth2DTO.getName() != null && googleUserInfo.getName() ==null){
            usersApp.setName(userRequestOAuth2DTO.getName());
        } else {
            throw new ValidationErrorWithMethod("Не передано имя пользователя");
        }
        if(userRequestOAuth2DTO.getSurname() == null && googleUserInfo.getSurname() != null){
            usersApp.setSurname(googleUserInfo.getSurname());
        } else if(userRequestOAuth2DTO.getSurname() != null && googleUserInfo.getSurname() ==null){
            usersApp.setSurname(userRequestOAuth2DTO.getSurname());
        } else {
            throw new ValidationErrorWithMethod("Не передана фамилия пользователя");
        }
        usersApp.setAge(userRequestOAuth2DTO.getAge());
        usersApp.setNickname(userRequestOAuth2DTO.getNickname());
        usersApp.setPassword(securityUsersService.passwordEncode(userRequestOAuth2DTO.getPassword()));
        Roles roles = rolesService.getRolesByName("USER");
        usersApp.rolesAdd(roles);
        usersApp.setEmail(googleUserInfo.getEmail());
        userRepository.save(usersApp);
        cache.evict(uuid);
        return new UserResponceDTO(usersApp, null);
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ Users ----------------------------->
    @Cacheable(value = "USER_RESPONSE_LIST", key = "#name + ' ' + #page")
    public ListUsersDTO getUserByName(String name, int page) {
        List<UsersApp> users = userRepository.findByNameContainingIgnoreCase(name, PageRequest.of(page, 10));
        if (users.isEmpty()) {
            throw new NoSuchElementException("Имя пользователя не найдено");
        }
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        users.forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp, imageUsersAppService.getImageName(usersApp))));
        return new ListUsersDTO(usersResponceDTOList);
    }

   @Cacheable(value = "USER_RESPONSE", key = "#nickname")
    public UserResponceDTO getUserByNickname(String nickname) {
        UsersApp usersApp = findUsersByNickname(nickname);
        return new UserResponceDTO(usersApp, imageUsersAppService.getImageName(usersApp));
    }

    @Cacheable(value = "USER_RESPONSE_LIST", key = "#ageOne + #ageTwo + #page")
    public ListUsersDTO getAgeUserBetween(int ageOne, int ageTwo, int page) {
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.findByAgeBetween(ageOne, ageTwo, PageRequest.of(page, 10)).forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))));
        return new ListUsersDTO(usersResponceDTOList);
    }

    @Transactional(readOnly = true)
    public ListCommunityUsersDTO getAllCommunityForUser(String nickname) {
        UsersApp usersApp = findUsersByNickname(nickname);
        List<Community> communityList = usersApp.getCommunity();
        List<CommunityResponseDTO> listCommunityUsersDTO = new ArrayList<>();
        communityList.forEach(community -> listCommunityUsersDTO.add(new CommunityResponseDTO(
                community.getName(),
                community.getDescription(),
                usersApp.getNickname(),
                community.getNickname(),
                imageCommunityService.getImageName(community))));
        return new ListCommunityUsersDTO(listCommunityUsersDTO);
    }

    // <----------------УДАЛЕНИЕ В СУЩНОСТИ Users ----------------------------->
    @Caching(evict = {
        @CacheEvict(value = "SECURITY", key="#nickname"),
        @CacheEvict(value = "USER_RESPONSE", key = "#nickname"),
        @CacheEvict(value = "FRIENDS_LIST", key = "#nickname"),
        @CacheEvict(value = "CHECK_FRIEND", key = "#nickname1 < #nickname2 ? #nickname1 + '_' + #nickname2 : #nickname2 + '_' + #nickname1")})
    @Transactional
    public void deleteUserByNickname(String nickname) throws IOException {
        UsersApp user = findUsersByNickname(nickname);
        imageUsersAppService.deleteImageUsersApp(user);
        for (PostsUserApp postsUserApp : user.getPostsUserAppList()){
            filePostsUsersAppService.deleteFileTapeUsersAppService(postsUserApp);
        }
        deleteCacheService.deleteAllCacheFriend(user.getId());
        userRepository.delete(user);
    }
    @CacheEvict(value = "USER_RESPONSE", key = "#nickname")
    @Transactional
    public void deleteImageUsersApp(String nickname) throws IOException {
        UsersApp user =  findUsersByNickname(nickname);
        deleteCacheService.deleteAllCacheFriend(user.getId());
        imageUsersAppService.deleteImageUsersApp(user);
    }


    @CacheEvict(value = "SECURITY", key="#addNewRoleUsersAppDTO.getNickname()")
    @Transactional
    public void deleteRolesUsersApp(AddNewRoleUsersAppDTO addNewRoleUsersAppDTO, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        if (addNewRoleUsersAppDTO.getNameRole().contains("ROLE_") || rolesRepository.findByName(addNewRoleUsersAppDTO.getNameRole()).isEmpty()){
            throw new ValidationErrorWithMethod("Роль передана не корректной");
        }
        UsersApp usersApp = findUsersByNickname(addNewRoleUsersAppDTO.getNickname());
        Set<Roles> roles = usersApp.getRoles().stream().filter(x -> !Objects.equals(x.getName(), addNewRoleUsersAppDTO.getNameRole())).collect(Collectors.toSet());
        usersApp.setRoles(roles);
        userRepository.save(usersApp);
    }

    // <----------------ПОИСК В СУЩНОСТИ Users ----------------------------->
    @Cacheable(value = "USER_RESPONSE_LIST", key="#name + '_' + #surname + '_' + #page")
    public ListUsersDTO findByNameAndSurname(String name, String surname, int page) {
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
    userRepository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name, surname, PageRequest.of(page, 10)).forEach(
            usersApp -> usersResponceDTOList.add(
                    new UserResponceDTO(usersApp,
                            imageUsersAppService.getImageName(usersApp))));
    return new ListUsersDTO(usersResponceDTOList);
    }

    public UsersApp findUsersByNickname(String nickname) {
        Optional<UsersApp> optionalUsers = userRepository.findByNickname(nickname);
        if (optionalUsers.isEmpty()) {
            throw new UsernameNotFoundException("Пользователей с таким nickname нет");
        }
        return optionalUsers.get();
    }

    public Long getIdWithNickname(String nickname){
        return userRepository.getUserIdByNickname(nickname).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с таким nickname нет")
        );
    }

    // <----------------ИЗМЕНЕНИЕ В СУЩНОСТИ Users ----------------------------->
    @CachePut(value = "USER_RESPONSE", key = "#result.getNickname()")
    @CacheEvict(value = "SECURITY", key="#nickname", condition = "#setUserDTO.getNicknameAfter() != null")
    @Transactional
    public UserResponceDTO setUsers(SetUserDTO setUserDTO, String nickname, BindingResult bindingResult, MultipartFile file) throws ValidationErrorWithMethod, IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        boolean flag = false;
        UsersApp user = findUsersByNickname(nickname);
        if(setUserDTO.getSurname() != null){
            setSurname(setUserDTO, user);
            flag = true;
        }
        if (setUserDTO.getName() != null) {
            setName(setUserDTO, user);
            flag = true;
        }
        if(file != null){
            setImageUsersApp(user, file);
            flag = true;
        }
        if(setUserDTO.getNicknameAfter() != null){
            setNickname(setUserDTO, user);
            Objects.requireNonNull(cacheManager.getCache("USER_RESPONSE")).evict(nickname);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
        deleteCacheService.deleteAllCacheFriend(user.getId());
        return new UserResponceDTO(user,  imageUsersAppService.getImageName(user));
    }
    public void setNickname(SetUserDTO apiResponseSetNicknameDTO, UsersApp user) {
        user.setNickname(apiResponseSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }

    public void setName(SetUserDTO setUserDTO,  UsersApp user){
        user.setName(setUserDTO.getName());
        userRepository.save(user);
    }

    public void setSurname(SetUserDTO setUserDTO,  UsersApp user){
        user.setSurname(setUserDTO.getSurname());
        userRepository.save(user);
    }

    @Transactional
    public void setImageUsersApp(UsersApp usersApp, MultipartFile file) throws IOException, ValidationErrorWithMethod {
        imageUsersAppService.setImagesUsersApp(file, usersApp);
    }


    @Transactional(readOnly = true)
    public void addRolesUsersApp(AddNewRoleUsersAppDTO addNewRoleUsersAppDTO, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp usersApp = findUsersByNickname(addNewRoleUsersAppDTO.getNickname());
        usersApp.rolesAdd(rolesService.getRolesByName(addNewRoleUsersAppDTO.getNameRole()));
        userRepository.save(usersApp);
    }

 }