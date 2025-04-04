package com.webapp.springBoot.service;

import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    private RolesRepository rolesRepository;

    // <-----------------Получение данных в сущность Roles--------------->
    public Roles getRolesByName(String name){
        Optional<Roles> optionalRoles = rolesRepository.findByName(name);
        if(optionalRoles.isEmpty()){
            throw  new NoSuchElementException("Роль не найдена");
        }
        return optionalRoles.get();
    }
    // <-----------------Добавление данных в сущность Roles--------------->
    public void addNewRoles(String nameRole) throws ValidationErrorWithMethod {
        if (nameRole.contains("ROLE_") || rolesRepository.findByName(nameRole).isPresent()){
            throw new ValidationErrorWithMethod("Роль передана не корректной");
        }
        Roles roles = new Roles();
        roles.setName(nameRole);
        rolesRepository.save(roles);
    }
}
