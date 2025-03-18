package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Community.CommunityResponseDTO;
import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;



@Service
public class UsersService {
    @Autowired
    private UsersAppRepository userRepository;
    @Autowired
    private ImageUsersAppService imageUsersAppService;
    @Autowired
    private ImageCommunityService imageCommunityService;


    public void saveUser(UserRequestDTO aPiResponceUserDTO, BindingResult result) throws ValidationErrorWithMethod {

        if(result.hasErrors()){
            throw  new ValidationErrorWithMethod(result.getAllErrors());
        }
        userRepository.save(new UsersApp(
                aPiResponceUserDTO.getName(),
                aPiResponceUserDTO.getSurname(),
                aPiResponceUserDTO.getAge(),
                aPiResponceUserDTO.getNickname()
        ));
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  Users ----------------------------->
    public ListUsersDTO getUserByName(String name){
        List<UsersApp> users = userRepository.findByNameContainingIgnoreCase(name);
        if (users.isEmpty()){
            throw new NoSuchElementException("Имя пользователя не найдено");
        }
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        users.forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,imageUsersAppService.getImageName(usersApp))
                )
        );
        return new ListUsersDTO(usersResponceDTOList);
    }

    public UserResponceDTO getUserByNickname(String nickname){
        UsersApp usersApp = findUsersByNickname(nickname);
        return new UserResponceDTO(usersApp, imageUsersAppService.getImageName(usersApp));
    }

    public ListUsersDTO getAllUser(){
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.findAll().forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))
                )
        );
        return new ListUsersDTO(usersResponceDTOList);
    }

    public ListUsersDTO getAgeUserBetween(int ageOne, int ageTwo){
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.getUsersByAgeBetween(ageOne, ageTwo).forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))
                )
        );
        return new ListUsersDTO(usersResponceDTOList);
    }

    @Transactional
    public ListCommunityUsersDTO getAllCommunityForUser(String nickname){
        UsersApp usersApp = findUsersByNickname(nickname);
        List<Community> communityList = usersApp.getCommunity();
        List<CommunityResponseDTO> listCommunityUsersDTO = new ArrayList<>();
        communityList.forEach(community ->
                    listCommunityUsersDTO.add(new CommunityResponseDTO(
                            community.getName(),
                            community.getDescription(),
                            usersApp.getNickname(),
                            community.getNickname(),
                            imageCommunityService.getImageName(community))
                    )
            );
        return new ListCommunityUsersDTO(listCommunityUsersDTO);
    }

    // <----------------УДАЛЕНИЕ В СУЩНОСТИ  Users ----------------------------->
    @Transactional
    public void deleteUserByNickname(String nickname) {
        Optional<UsersApp> users = userRepository.findByNickname(nickname);
        if (users.isEmpty()){
            throw new NoSuchElementException("Nickname пользователя не найден");
        }
        userRepository.delete(users.get());
    }

    @Transactional
    public void deleteImageUsersApp(String nickname) throws IOException {
        imageUsersAppService.deleteImageUsersApp(findUsersByNickname(nickname));
    }

    // <----------------ПОИСК В СУЩНОСТИ  Users ----------------------------->
    public ListUsersDTO findByNameAndSurname(String name, String surname){
        List<UserResponceDTO> usersResponceDTOList = new ArrayList<>();
        userRepository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name, surname).forEach(
                usersApp -> usersResponceDTOList.add(
                        new UserResponceDTO(usersApp,
                                imageUsersAppService.getImageName(usersApp))
                )
        );
        return new ListUsersDTO(usersResponceDTOList);
    }

    public UsersApp findUsersByNickname(String nickname){
        Optional<UsersApp> optionalUsers = userRepository.findByNickname(nickname);
        if (optionalUsers.isEmpty()){
            throw new NoSuchElementException("Пользователей с таким nickname нет");
        }
        return optionalUsers.get();
    }

    // <----------------ИЗМЕНЕНИЕ В СУЩНОСТИ  Users ----------------------------->
    public void setNickname (SetNicknameDTO apiResponceSetNicknameDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp user = findUsersByNickname(apiResponceSetNicknameDTO.getNicknameBefore());
        user.setNickname(apiResponceSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }

    public void setName (SetNameDTO setNameDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp user = findUsersByNickname(setNameDTO.getNickname());
        user.setName(setNameDTO.getNameAfter());
        userRepository.save(user);
    }

    public void setSurname (SetSurnameDTO setSurnameDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp user = findUsersByNickname(setSurnameDTO.getNickname());
        user.setSurname(setSurnameDTO.getSurnameAfter());
        userRepository.save(user);
    }
    @Transactional
    public void setImageUsersApp(MultipartFile file, String nickname) throws IOException, ValidationErrorWithMethod {
        imageUsersAppService.setImagesUsersApp(file, findUsersByNickname(nickname));
    }



}
