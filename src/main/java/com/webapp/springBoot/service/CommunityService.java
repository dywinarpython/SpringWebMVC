package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Community.*;
import com.webapp.springBoot.DTO.Users.ListCommunityUsersDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.CommunityRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;


    @Autowired
    private UsersService usersService;


    @Autowired
    private ImageCommunityService imageCommunityService;

    @Autowired
    private FilePostsCommunityService filePostsCommunityService;

    @Autowired
    private CacheManager cacheManager;



    // <----------------ПРОВЕРКА ДАННЫХ В СУЩНОСТИ  Community ----------------------------->
    public Community checkCommunityByNicknameUser(String nickname, String nicknameUser){
        Community community = findCommunityByNickname(nickname);
        if(community.getUserOwner().equals(usersService.findUsersByNickname(nicknameUser))){
            return community;
        } else {
            throw new LockedException("Пользователь не имеет право управлять данным сообществом");
        }
    }
    public void checkCommunityByNicknameUser(Community community, String nicknameUser){
        if(!community.getUserOwner().equals(usersService.findUsersByNickname(nicknameUser))){
            throw new LockedException("Пользователь не имеет право управлять данным сообществом");
        }
    }

    // <----------------СОЗДАНИЯ ДАННЫХ В СУЩНОСТИ  Community ----------------------------->
    @CachePut(value = "COMMUNITY_RESPONSE", key="#communityDTO.getNicknameCommunity()")
    @Transactional
    public CommunityResponseDTO addNewCommunity(CommunityRequestDTO communityDTO,String nickameUser, BindingResult bindingResult) throws ValidationErrorWithMethod {
        if (bindingResult.hasErrors()){
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        UsersApp userApp = usersService.findUsersByNickname(nickameUser);
        if(userApp.getCommunity().size() < 3) {
            Community community = new Community(userApp, communityDTO.getDescription(), communityDTO.getName(), communityDTO.getNicknameCommunity());
            return new CommunityResponseDTO(communityRepository.save(community), imageCommunityService.getImageName(community));
        } else {
            throw new ValidationErrorWithMethod("Пользователь не может иметь более 3 сообществ");
        }
    }

    // <----------------ПОЛУЧЕНИЕ ДАННЫХ В СУЩНОСТИ  Community ----------------------------->

    @Cacheable(value = "COMMUNITY_RESPONSE", key="#nickname")
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
    @Cacheable(value = "COMMUNITY_RESPONSE_LIST", key="#page")
    public ListCommunityDTO getСommunity(int page){
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
        return new ListCommunityDTO(communityResponceDTOList);
    }
    @Cacheable(value = "COMMUNITY_RESPONSE_LIST", key="#name + ' ' + #page")
    public  ListCommunityDTO findByNameLike(String name, int page){
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
        return new ListCommunityDTO(communityResponceDTOList);
    }


    // <----------------УДАЛЕНИЕ  В СУЩНОСТИ  Community ----------------------------->
    @CacheEvict(value = "COMMUNITY_RESPONSE", key = "#nickname")
    @Transactional
    public void deleteCommunityByNickname(String nickname, String nicknameUser) throws IOException{
        Community community = checkCommunityByNicknameUser(nickname, nicknameUser);
            community.getUserOwner().setCommunity(null);
            imageCommunityService.deleteImageCommunity(community);
            for (PostsCommunity postsCommunity : community.getPostsCommunityList()){
                filePostsCommunityService.deleteFilePostsCommunityService(postsCommunity);
            }
            communityRepository.delete(community);
    }

    @Transactional
    public void deleteImageCommunity(String nickname, String nicknameUser) throws IOException {
        imageCommunityService.deleteImageCommunity(checkCommunityByNicknameUser(nickname, nicknameUser));
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

    @CachePut(value = "COMMUNITY_RESPONSE", key="#result.getNicknameCommunity()")
    @Transactional
    public CommunityResponseDTO setCommunity(SetCommunityDTO setCommunityDTO,String nicknameUser, BindingResult bindingResult, MultipartFile file) throws ValidationErrorWithMethod, IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        boolean flag = false;
        Community community = checkCommunityByNicknameUser(setCommunityDTO.getNickname(),nicknameUser);
        if(setCommunityDTO.getDescription() != null){
            setDescription(setCommunityDTO, community);
            flag = true;
        }
        if (setCommunityDTO.getName() != null) {
            setName(setCommunityDTO, community);
            flag = true;
        }
        if(file != null){
            setImage(setCommunityDTO,community, file);
            flag = true;
        }
        if(setCommunityDTO.getNicknameAfter() != null){
            setNickname(setCommunityDTO, community);
            Objects.requireNonNull(cacheManager.getCache("USER_RESPONSE")).evict(setCommunityDTO.getNickname());
            flag = true;
        }
        if(!flag){
            throw new ValidationErrorWithMethod("Нет даных для обновления");
        }
        return new CommunityResponseDTO(community,  imageCommunityService.getImageName(community));
    }

    public void setDescription(SetCommunityDTO setCommunityDTO, Community community)  {
        community.setDescription(setCommunityDTO.getDescription());
        communityRepository.save(community);
    }
    public void setNickname(SetCommunityDTO setNicknameCommunity, Community community) {
        community.setNickname(setNicknameCommunity.getNicknameAfter());
        communityRepository.save(community);
    }
    public void setName(SetCommunityDTO setNameCommunityDTO, Community community){
        community.setName(setNameCommunityDTO.getName());
        communityRepository.save(community);
    }
    @Transactional
    public void setImage(SetCommunityDTO setCommunityDTO,Community community, MultipartFile file) throws IOException, ValidationErrorWithMethod {
        imageCommunityService.setImagesCommunity(file, community);
    }
}
