package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Community.CommunityDTO;
import com.webapp.springBoot.DTO.Community.SetDescriptionCommunityDTO;
import com.webapp.springBoot.DTO.Community.SetNameCommunityDTO;
import com.webapp.springBoot.DTO.Community.SetNicknameCommunityDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.ImagesCommunity;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.validation.File.UploadFileValidation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;


    @Autowired
    private UsersService usersService;


    @Autowired
    private ImageCommunityService imageCommunityService;





    public void addNewCommunity(CommunityDTO communityDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp userApp = usersService.findByNickname(communityDTO.getNicknameUser());
        Community community = new Community(userApp, communityDTO.getDescription(), communityDTO.getName(), communityDTO.getNicknameCommunity());
        communityRepository.save(community);
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  Community ----------------------------->
    public List<Community> getAllCommunity(){
        return communityRepository.findAll();
    }
    public  List<Community> findByNameLike(String name){
        return communityRepository.findByNameLike(name);
    }


    // <----------------УДАЛЕНИЕ  В СУЩНОСТИ  Community ----------------------------->
    @Transactional
    public void deleteCommunityByNickname(String nickname){
        Community community = findCommunityByNickname(nickname);
        community.getUserOwnerId().setCommunity(null);
        communityRepository.delete(community);
    }

    public void deleteImageCommunity(String nickname) throws IOException {
        imageCommunityService.deleteImageCommunity(nickname);
    }
    // <----------------ПОИСК В СУЩНОСТИ  Community ----------------------------->
    public Community findCommunityByNickname(String nickname){
        Optional<Community> communityOptional = communityRepository.findByNickname(nickname);
        if (communityOptional.isEmpty()){
            throw new NoSuchElementException("Nickname сообщества не найден");
        }
        return communityOptional.get();
    }

    // <----------------ИЗМЕНЕНИЕ  В СУЩНОСТИ  Community ----------------------------->
    public void setDescriptionCommunity(SetDescriptionCommunityDTO setDescriptionCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        Community community = findCommunityByNickname(setDescriptionCommunityDTO.getNickname());
        community.setDescription(setDescriptionCommunityDTO.getDescription());
        communityRepository.save(community);
    }

    public void setNicknameCommunity(SetNicknameCommunityDTO setNicknameCommunity, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        Community community = findCommunityByNickname(setNicknameCommunity.getNicknameBefore());
        community.setNickname(setNicknameCommunity.getNicknameAfter());
        communityRepository.save(community);
    }

    public void setNameCommunity(SetNameCommunityDTO setNameCommunityDTO, BindingResult result) throws ValidationErrorWithMethod {
        if(result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        Community community = findCommunityByNickname(setNameCommunityDTO.getNickname());
        community.setName(setNameCommunityDTO.getName());
        communityRepository.save(community);
    }

    public void setImageCommunity(MultipartFile file, String nickname) throws IOException, ValidationErrorWithMethod {
        imageCommunityService.createImagesCommunty(nickname, file);
    }




}
