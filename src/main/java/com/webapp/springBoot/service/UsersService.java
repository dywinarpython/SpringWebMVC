package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Person.APiResponceUserDTO;
import com.webapp.springBoot.DTO.Person.ApiResponceSetNicknameDTO;
import com.webapp.springBoot.entity.Users;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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




    public void saveUser( APiResponceUserDTO aPiResponceUserDTO, BindingResult result) throws ValidationErrorWithMethod {

        if(result.hasErrors()){
            throw  new ValidationErrorWithMethod(result.getAllErrors());
        }
        userRepository.save(new Users(
                aPiResponceUserDTO.getName(),
                aPiResponceUserDTO.getSurname(),
                aPiResponceUserDTO.getAge(),
                aPiResponceUserDTO.getNickname()
        ));
    }

    public List<Users> getUserByName(String name){
        List<Users> users = userRepository.findByName(name);
        if (users.isEmpty()){
            throw new NoSuchElementException("Имя пользователя не найдено");
        }
        return users;
    }


    public List<Users> getAllUser(){
        return userRepository.findAll();
    }

    public List<Users> getAgeUserBetween(int ageOne, int ageTwo){
        return userRepository.getUsersByAgeBetween(ageOne, ageTwo);
    }

    @Transactional
    public void deleteUserByID(Long id) throws NoSuchElementException {
        Optional<Users> users = userRepository.findById(id);
        if (users.isEmpty()){
            throw new NoSuchElementException("ID пользователя не найден");
        }
        userRepository.delete(users.get());
    }

    public List<Users> findByNameAndSurname(String name, String surname){
        List<Users> users = userRepository.findByNameAndSurname(name, surname);
        System.out.println(users);
        if (users.isEmpty()){
            throw new NoSuchElementException("Пользователей с таким именем и фамилией нет");
        }
        return users;
    }

    public Users findByNickname(String nickname){
        Optional<Users> optionalUsers = userRepository.findByNickname(nickname);
        if (optionalUsers.isEmpty()){
            throw new NoSuchElementException("Пользователей с таким nickname нет");
        }
        return optionalUsers.get();
    }


    public void setNickname (ApiResponceSetNicknameDTO apiResponceSetNicknameDTO, BindingResult result){
        if (result.hasErrors()){
            throw new NoSuchElementException("Не корректный nickname");
        }
        Users user = findByNickname(apiResponceSetNicknameDTO.getNicknameBefore());
        user.setNickname(apiResponceSetNicknameDTO.getNicknameAfter());
        userRepository.save(user);
    }



}
