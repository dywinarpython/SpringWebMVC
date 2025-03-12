package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Users.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;



@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;


    public void saveUser(UserDTO aPiResponceUserDTO, BindingResult result) throws ValidationErrorWithMethod {

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
    public List<UsersApp> getUserByName(String name){
        List<UsersApp> users = userRepository.findByNameContaining(name);
        if (users.isEmpty()){
            throw new NoSuchElementException("Имя пользователя не найдено");
        }
        return users;
    }

    public List<UsersApp> getAllUser(){
        return userRepository.findAll();
    }

    public List<UsersApp> getAgeUserBetween(int ageOne, int ageTwo){
        return userRepository.getUsersByAgeBetween(ageOne, ageTwo);
    }

    @Transactional
    public ListCommunityUsersDTO getAllCommunityForUser(String nickname){
        UsersApp usersApp = findByNickname(nickname);
        List<Community> communityList = usersApp.getCommunity();
        List<CommunityUsersDTO> listCommunityUsersDTO = communityList.stream().map(community ->
                    new CommunityUsersDTO(
                            community.getName(),
                            community.getDescription(),
                            community.getNickname()
                    )
            ).toList();
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

    // <----------------ПОИСК В СУЩНОСТИ  Users ----------------------------->
    public List<UsersApp> findByNameAndSurname(String name, String surname){
        List<UsersApp> users = userRepository.findByNameContainingAndSurnameContaining(name, surname);
        if (users.isEmpty()){
            throw new NoSuchElementException("Пользователей с таким именем и фамилией нет");
        }
        return users;
    }

    public UsersApp findByNickname(String nickname){
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
        UsersApp user = findByNickname(apiResponceSetNicknameDTO.getNicknameBefore());
        user.setNickname(apiResponceSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }

    public void setName (SetNameDTO setNameDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp user = findByNickname(setNameDTO.getNickname());
        user.setName(setNameDTO.getNameAfter());
        userRepository.save(user);
    }

    public void setSurname (SetSurnameDTO setSurnameDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp user = findByNickname(setSurnameDTO.getNickname());
        user.setSurname(setSurnameDTO.getSurnameAfter());
        userRepository.save(user);
    }



}
