package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Person.UserDTO;
import com.webapp.springBoot.DTO.Person.SetNicknameDTO;
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

    public List<UsersApp> getUserByName(String name){
        List<UsersApp> users = userRepository.findByName(name);
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
    public void deleteUserByNickname(String nickname) {
        Optional<UsersApp> users = userRepository.findByNickname(nickname);
        if (users.isEmpty()){
            throw new NoSuchElementException("Nickname пользователя не найден");
        }
        userRepository.delete(users.get());
    }

    public List<UsersApp> findByNameAndSurname(String name, String surname){
        List<UsersApp> users = userRepository.findByNameAndSurname(name, surname);
        System.out.println(users);
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


    public void setNickname (SetNicknameDTO apiResponceSetNicknameDTO, BindingResult result){
        if (result.hasErrors()){
            throw new NoSuchElementException("Не корректный nickname");
        }
        UsersApp user = findByNickname(apiResponceSetNicknameDTO.getNicknameBefore());
        user.setNickname(apiResponceSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }



}
