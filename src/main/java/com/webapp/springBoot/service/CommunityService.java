package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Community.*;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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






    @Transactional
    public void addNewCommunity(CommunityRequestDTO communityDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp userApp = usersService.findUsersByNickname(communityDTO.getNicknameUser());
        if(userApp.getCommunity().size() < 3) {
            Community community = new Community(userApp, communityDTO.getDescription(), communityDTO.getName(), communityDTO.getNicknameCommunity());
            communityRepository.save(community);
        } else {
            throw new ValidationErrorWithMethod("Пользователь не может иметь более 3 сообществ");
        }
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  Community ----------------------------->
    
    public CommunityResponseDTO getByNickname(String nickname){
        Community community = findCommunityByNickname(nickname);
        return new CommunityResponseDTO(
                community.getName(),
                community.getDescription(),
                community.getUserOwner().getNickname(),
                community.getNickname(),
                imageCommunityService.getImageName(community)
        );
    }
    public List<CommunityResponseDTO> getСommunity(int page){
        List<CommunityResponseDTO> communityResponceDTOList = new ArrayList<>();
        communityRepository.findByOrderByName(PageRequest.of(page, 10)).forEach(
                community -> communityResponceDTOList.add(
                        new CommunityResponseDTO(
                                community.getName(),
                                community.getDescription(),
                                community.getUserOwner().getNickname(),
                                community.getNickname(),
                                imageCommunityService.getImageName(community)
                        )
                )
        );
        return communityResponceDTOList;
    }
    public  List<CommunityResponseDTO> findByNameLike(String name, int page){
        List<CommunityResponseDTO> communityResponceDTOList = new ArrayList<>();
        communityRepository.findByNameContainsIgnoreCaseOrderByName(name, PageRequest.of(page, 10)).forEach(
                community -> communityResponceDTOList.add(
                        new CommunityResponseDTO(
                                community.getName(),
                                community.getDescription(),
                                community.getUserOwner().getNickname(),
                                community.getNickname(),
                                imageCommunityService.getImageName(community)
                        )
                )
        );
        return communityResponceDTOList;
    }


    // <----------------УДАЛЕНИЕ  В СУЩНОСТИ  Community ----------------------------->
    @Transactional
    public void deleteCommunityByNickname(String nickname) throws IOException{
        Community community = findCommunityByNickname(nickname);
        community.getUserOwner().setCommunity(null);
        imageCommunityService.deleteImageCommunity(community);
        communityRepository.delete(community);
    }
    @Transactional
    public void deleteImageCommunity(String nickname) throws IOException {
        imageCommunityService.deleteImageCommunity(findCommunityByNickname(nickname));
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
    @Transactional
    public void setCommunity(SetCommunityDTO setCommunityDTO, BindingResult result, MultipartFile file) throws ValidationErrorWithMethod, IOException {
        boolean flag = false;
        if (result.hasErrors()) {
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        if(setCommunityDTO.getDescription() != null){
            setDescription(setCommunityDTO);
            flag = true;
        }
        if (setCommunityDTO.getName() != null) {
            setName(setCommunityDTO);
            flag = true;
        }
        if(file != null){
            setImage(setCommunityDTO, file);
            flag = true;
        }
        if(setCommunityDTO.getNicknameAfter() != null){
            setNickname(setCommunityDTO);
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
    }

    public void setDescription(SetCommunityDTO setCommunityDTO)  {
        Community community = findCommunityByNickname(setCommunityDTO.getNickname());
        community.setDescription(setCommunityDTO.getDescription());
        communityRepository.save(community);
    }
    public void setNickname(SetCommunityDTO setNicknameCommunity) {
        Community community = findCommunityByNickname(setNicknameCommunity.getNickname());
        community.setNickname(setNicknameCommunity.getNicknameAfter());
        communityRepository.save(community);
    }
    public void setName(SetCommunityDTO setNameCommunityDTO){
        Community community = findCommunityByNickname(setNameCommunityDTO.getNickname());
        community.setName(setNameCommunityDTO.getName());
        communityRepository.save(community);
    }
    @Transactional
    public void setImage(SetCommunityDTO setCommunityDTO, MultipartFile file) throws IOException, ValidationErrorWithMethod {
        imageCommunityService.setImagesCommunity(file, findCommunityByNickname(setCommunityDTO.getNickname()));
    }
}
