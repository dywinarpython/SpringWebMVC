package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.UserReaction.ListUserReactionDTO;
import com.webapp.springBoot.DTO.UserReaction.RequestUserReactionDTO;
import com.webapp.springBoot.DTO.UserReaction.UserReactionDTO;
import com.webapp.springBoot.cache.DeleteCacheService;
import com.webapp.springBoot.entity.UserPostReaction;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.UserPostReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class UserPostReactionService {

    @Autowired
    private UserPostReactionRepository userPostReactionRepository;

    @Autowired
    private UsersService usersService;


    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DeleteCacheService deleteCacheService;

    @CacheEvict(value = "POST", key="#requestUserReactionDTO.getNamePost()")
    @Transactional
    public void createUserReaction(RequestUserReactionDTO requestUserReactionDTO, BindingResult bindingResult, String nickname) throws ValidationErrorWithMethod {
        if(bindingResult.hasErrors()){
            throw new ValidationErrorWithMethod(bindingResult.getAllErrors());
        }
        if(requestUserReactionDTO.getReaction().equals(0)){
            throw new ValidationErrorWithMethod("Реакия не может быть ни чем, она либо положительная, либо отрицательная");
        }
        if(userPostReactionRepository.existsByUsersApp_IdAndNamePost(usersService.getIdWithNickname(nickname), requestUserReactionDTO.getNamePost()) ||
                !userPostReactionRepository.checkCreator(nickname, requestUserReactionDTO.getNamePost())){
            throw new ValidationErrorWithMethod("Ошибка создания реакции пользователя");
        }
        UserPostReaction userPostReaction = new UserPostReaction();
        userPostReaction.setUsersApp(usersService.findUsersByNickname(nickname));
        userPostReaction.setNamePost(requestUserReactionDTO.getNamePost());
        userPostReaction.setRating(requestUserReactionDTO.getReaction());
        Cache cache = cacheManager.getCache("REACTION");
        if(cache == null){
            log.error("Не возможно создать реакцию пользователя, кеш не доступен");
            throw new RuntimeException("Кеш не доступен");
        }
        userPostReactionRepository.save(userPostReaction);
        userPostReactionRepository.updateRatingForPost(requestUserReactionDTO.getNamePost(), requestUserReactionDTO.getReaction());
        cache.put(nickname + ':' + requestUserReactionDTO.getNamePost(), requestUserReactionDTO.getReaction());
    }

    @Transactional
    @Caching(evict = {
    @CacheEvict(value = "POST", key = "#namePost"),
    @CacheEvict(value = "REACTION", key = "#nickname + ':' + #namePost")}
    )
    public void deleteUserReaction(String nickname, String namePost){
        UserPostReaction userPostReaction = userPostReactionRepository.findByUsersApp_NicknameAndNamePost(nickname,namePost).orElseThrow(
                () -> new NoSuchElementException("Реакция пользователя не найдена")
        );
        userPostReactionRepository.updateRatingForPost(namePost, -userPostReaction.getRating());
        userPostReactionRepository.delete(userPostReaction);
    }

    @Transactional
    @CacheEvict(value = "REACTION", key = "#nickname + ':' + #namePost")
    public void deleteUserReactionByNamePost(String namePost){
        List<UserPostReaction> userPostReactions = userPostReactionRepository.findByNamePost(namePost);
        deleteCacheService.deleteAllReaction(userPostReactions);
        userPostReactionRepository.deleteAll(userPostReactions);
    }

    @Transactional
    public void updateUserReaction(RequestUserReactionDTO requestUserReactionDTO, BindingResult bindingResult, String nickname) throws ValidationErrorWithMethod {
        deleteUserReaction(nickname, requestUserReactionDTO.getNamePost());
        createUserReaction(requestUserReactionDTO, bindingResult, nickname);
    }


    @Cacheable(value = "REACTION", key = "#nickname + ':' + #namePost")
    public Integer getRating(String nickname, String namePost){
        Optional<UserPostReaction> userPostReaction = userPostReactionRepository.findByUsersApp_NicknameAndNamePost(nickname, namePost);
        return userPostReaction.map(UserPostReaction::getRating).orElse(0);
    }
    public ListUserReactionDTO getRating(String nickname, List<String> namePost){
        return new ListUserReactionDTO(userPostReactionRepository.findByUsersApp_NicknameAndNamePost(nickname, namePost));
    }
}
