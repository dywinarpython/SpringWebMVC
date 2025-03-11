package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Community.CommunityDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UsersService usersService;



    public void addNewCommunity(CommunityDTO communityDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp userApp = usersService.findByNickname(communityDTO.getNicknameUser());
        communityRepository.save(
                new Community(userApp, communityDTO.getDescription(), communityDTO.getName(), communityDTO.getNicknameCommunity())
        );
    }

    public List<Community> getAllCommunity(){
        return communityRepository.findAll();
    }
    public  List<Community> findByNameLike(String name){
        return communityRepository.findByNameLike(name);
    }

    @Transactional
    public void deleteCommunityByNickname(String nickname){
        Optional<Community> optionalCommunity = communityRepository.findByNickname(nickname);
        if (optionalCommunity.isEmpty()){
            throw new NoSuchElementException("Nickname сообщества не найден");
        }
        communityRepository.delete(optionalCommunity.get());
    }

    public void setNameCommunity(String name, String nickname){
        Optional<Community> optionalCommunity = communityRepository.findByNickname(nickname);
        if (optionalCommunity.isEmpty()){
            throw new NoSuchElementException("Nickname сообщества не найден");
        }
        Community community = optionalCommunity.get();
        community.setName(name);
        communityRepository.save(community);

    }
    public void setDescriptionCommunity(String description, String nickname){
        Optional<Community> optionalCommunity = communityRepository.findByNickname(nickname);
        if (optionalCommunity.isEmpty()){
            throw new NoSuchElementException("Nickname сообщества не найден");
        }
        Community community = optionalCommunity.get();
        community.setName(description);
        communityRepository.save(community);
    }

}
