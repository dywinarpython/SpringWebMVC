package com.webapp.springBoot.service;

import com.webapp.springBoot.DTO.Admin.AddNewRoleUsersAppDTO;
import com.webapp.springBoot.DTO.Community.CommunityResponseDTO;
import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.RolesRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.security.SecurityUsersService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public void saveUser(UserRequestDTO aPiResponceUserDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        Roles roles = rolesService.getRolesByName("USER");
        UsersApp usersApp = new UsersApp();
        usersApp.setName(aPiResponceUserDTO.getName());
        usersApp.setSurname(aPiResponceUserDTO.getSurname());
        usersApp.setAge(aPiResponceUserDTO.getAge());
        usersApp.setNickname(aPiResponceUserDTO.getNickname());
        usersApp.setPassword(securityUsersService.passwordEncode(aPiResponceUserDTO.getPassword()));
        usersApp.setRoles(roles);
        usersApp.setPhoneNumber(aPiResponceUserDTO.getPhone());
        userRepository.save(usersApp);
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ Users ----------------------------->
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

    public UserResponceDTO getUserByNickname(String nickname) {
        UsersApp usersApp = findUsersByNickname(nickname);
        return new UserResponceDTO(usersApp, imageUsersAppService.getImageName(usersApp));
    }

    public ListUsersDTO getUsers(int page) {
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.findByOrderByNameAscSurnameAsc(PageRequest.of(page, 10)).forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))));
        return new ListUsersDTO(usersResponceDTOList);
    }

    public ListUsersDTO getAgeUserBetween(int ageOne, int ageTwo, int page) {
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.findByAgeBetween(ageOne, ageTwo, PageRequest.of(page, 10)).forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))));
        return new ListUsersDTO(usersResponceDTOList);
    }

    @Transactional
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
    @Transactional
    public void deleteUserByNickname(String nickname) throws IOException {
        Optional<UsersApp> users = userRepository.findByNickname(nickname);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Nickname пользователя не найден");
        }
        UsersApp usersApp = users.get();
        imageUsersAppService.deleteImageUsersApp(usersApp);
        userRepository.delete(usersApp);
    }
    @Transactional
    public void deleteImageUsersApp(String nickname) throws IOException {
        imageUsersAppService.deleteImageUsersApp(findUsersByNickname(nickname));
    }


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

    // <----------------ИЗМЕНЕНИЕ В СУЩНОСТИ Users ----------------------------->

    @Transactional
    public void setUsers(SetUserDTO setUserDTO, String nickname, BindingResult result, MultipartFile file) throws ValidationErrorWithMethod, IOException {
        boolean flag = false;
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        if(setUserDTO.getSurname() != null){
            setSurname(setUserDTO, nickname);
            flag = true;
        }
        if (setUserDTO.getName() != null) {
            setName(setUserDTO, nickname);
            flag = true;
        }
        if(file != null){
            setImageUsersApp(nickname, file);
            flag = true;
        }
        if(setUserDTO.getNicknameAfter() != null){
            setNickname(setUserDTO, nickname);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
    }
    public void setNickname(SetUserDTO apiResponceSetNicknameDTO, String nickname) {

        UsersApp user = findUsersByNickname(nickname);
        user.setNickname(apiResponceSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }

    public void setName(SetUserDTO setUserDTO, String nickname){

        UsersApp user = findUsersByNickname(nickname);
        user.setName(setUserDTO.getName());
        userRepository.save(user);
    }

    public void setSurname(SetUserDTO setUserDTO, String nickname){
        UsersApp user = findUsersByNickname(nickname);
        user.setSurname(setUserDTO.getSurname());
        userRepository.save(user);
    }

    @Transactional
    public void setImageUsersApp(String nickname, MultipartFile file) throws IOException, ValidationErrorWithMethod {
        imageUsersAppService.setImagesUsersApp(file, findUsersByNickname(nickname));
    }


    public void addRolesUsersApp(AddNewRoleUsersAppDTO addNewRoleUsersAppDTO, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp usersApp = findUsersByNickname(addNewRoleUsersAppDTO.getNickname());
        usersApp.setRoles(rolesService.getRolesByName(addNewRoleUsersAppDTO.getNameRole()));
        userRepository.save(usersApp);
    }

}
