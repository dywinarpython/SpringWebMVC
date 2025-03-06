package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.Users;
import com.webapp.springBoot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;

    public void saveUser(Users users){
        userRepository.save(users);
    }

    public List<Users> getUserByName(String name){
        return userRepository.findByName(name);
    }

    public List<Users> getAllUser(){
        return userRepository.findAll();
    }

    @Transactional
    public Users deleteUserByID(Long id) throws Exception {
        Optional<Users> users = userRepository.findById(id);
        if (users.isEmpty()){
            throw new Exception("Пользователь не найден");
        }
        Users user = users.get();
        userRepository.delete(user);
        return user;
    }

}
