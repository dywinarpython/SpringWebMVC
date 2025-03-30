package com.webapp.springBoot.service;

import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    private RolesRepository rolesRepository;

    public Roles getRolesByName(String name){
        Optional<Roles> optionalRoles = rolesRepository.findByName(name);
        if(optionalRoles.isEmpty()){
            throw  new NoSuchElementException("Роль не найдена");
        }
        return optionalRoles.get();
    }
}
