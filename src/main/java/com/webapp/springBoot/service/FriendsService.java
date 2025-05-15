package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.DTO.Friend.ResponseFriendDTO;
import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.FriendsRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;


    @Autowired
    private UsersService usersService;

    @Autowired
    private ImageUsersAppService imageUsersAppService;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private CacheManager cacheManager;


    // Первое обязательно Principal
    public Boolean checkFriend(String nickname1, String nickname2) {
        Cache cache = cacheManager.getCache("CHECK_FRIEND");
        assert cache != null;
        if(Boolean.TRUE.equals(cache.get(nickname1.compareTo(nickname2) < 0 ? nickname1 + '_' + nickname2 : nickname2 + '_' + nickname1, Boolean.class))){
            return true;
        }
        return friendsRepository.checkFriends(usersService.getIdWithNickname(nickname1), usersService.getIdWithNickname(nickname2));
    }

    @Caching(
            put = {
                    @CachePut(value = "CHECK_FRIEND", key = "#nickname1 < #nickname2 ? #nickname1 + '_' + #nickname2 : #nickname2 + '_' + #nickname1")
            },
            evict = {
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname2"),
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname1"),
            }
    )
    public Boolean createFriend(String nickname1, String nickname2) throws ValidationErrorWithMethod {
        if(checkFriend(nickname1, nickname2) || nickname1.equals(nickname2)){
            log.error(STR."\{nickname1}_\{nickname2}");
                throw new ValidationErrorWithMethod("Ошибка добавления друга");
        };
        UsersApp usersApp = usersService.findUsersByNickname(nickname1);
        UsersApp friend = usersService.findUsersByNickname(nickname2);
        Friends friends = new Friends();
        friends.setUsersApp(usersApp);
        friends.setFriendUsersApp(friend);
        Friends friends2 = new Friends();
        friends2.setUsersApp(friend);
        friends2.setFriendUsersApp(usersApp);
        friendsRepository.save(friends);
        friendsRepository.save(friends2);
        return true;
    }

    @Cacheable(value = "FRIENDS_LIST", key = "#nickname")
    @Transactional(readOnly = true)
    public ListResponseFriendDTO getAllFriend(String nickname){
        List<ResponseFriendDTO> friendsList = friendsRepository.getFriends(usersService.getIdWithNickname(nickname));
        return new ListResponseFriendDTO(friendsList);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname2"),
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname1"),
                    @CacheEvict(value = "CHECK_FRIEND", key = "#nickname1 < #nickname2 ? #nickname1 + '_' + #nickname2 : #nickname2 + '_' + #nickname1"),
                    @CacheEvict(value = "FRIEND_LIST_NICKNAME", key="#nickname1"),
                    @CacheEvict(value = "FRIEND_LIST_NICKNAME", key="#nickname2"),
            }
    )
    @Transactional
    public void deleteFriend(String nickname1, String nickname2){
        UsersApp usersApp = usersService.findUsersByNickname(nickname1);
        UsersApp usersAppFriend = usersService.findUsersByNickname(nickname2);

        Friends friends = usersApp.getFriends().stream().filter(friend -> friend.getFriendUsersApp().equals(usersAppFriend)).findFirst().orElseThrow( () -> new NoSuchElementException("Дружбы между пользователями нет"));
        usersApp.getFriends().remove(friends);
        friendsRepository.delete(friends);
        usersAppRepository.save(usersApp);

        friends =  usersAppFriend.getFriends().stream().filter(friend -> friend.getFriendUsersApp().equals(usersApp)).findFirst().orElseThrow(() -> new NoSuchElementException("Дружбы между пользователями нет"));
        usersAppFriend.getFriends().remove(friends);
        friendsRepository.delete(friends);
        usersAppRepository.save(usersAppFriend);
    }

}

