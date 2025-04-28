package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.DTO.Friend.ResponseFriendDTO;
import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.FriendsRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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


    // Первое обязательно Principal
    @Cacheable(value = "CHECK_FRIEND", key="#nickname1 + '_' + #nickname2")
    public void checkFriend(String nickname1, String nickname2){
        UsersApp usersApp = usersService.findUsersByNickname(nickname1);
        UsersApp friend = usersService.findUsersByNickname(nickname2);
        if(!friendsRepository.checkFriends(usersApp.getId(), friend.getId())){
            throw new NoSuchElementException("Пользователи не друзья");
        }
    }

    public void createFriend(String nickname1, String nickname2){
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
    }

    @Cacheable(value = "FRIENDS_LIST", key = "#nickname")
    @Transactional(readOnly = true)
    public ListResponseFriendDTO getAllFriend(String nickname){
        UsersApp usersApp = usersService.findUsersByNickname(nickname);
        List<Friends> friendsList = friendsRepository.getFriends(usersApp.getId());
        List<ResponseFriendDTO> responseFriendDTOList = new ArrayList<>();
        friendsList.forEach(friends -> {
            UsersApp friend = friends.getFriendUsersApp();
            responseFriendDTOList.add(new ResponseFriendDTO(
                    friend.getNickname(),
                    friend.getName(),
                    friend.getSurname(),
                    imageUsersAppService.getImageName(friend)
            ));
        });
        return new ListResponseFriendDTO(responseFriendDTOList);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname2"),
                    @CacheEvict(value = "FRIENDS_LIST", key="#nickname1"),
                    @CacheEvict(value = "CHECK_FRIEND", key="#nickname1 + ' ' + #nickname2")
            }
    )
    @Transactional
    public void deleteFriend(String nickname1, String nickname2){
        UsersApp usersApp = usersService.findUsersByNickname(nickname1);
        UsersApp usersAppFriend = usersService.findUsersByNickname(nickname2);
        List<Friends> friendsList = new ArrayList<>(usersApp.getFriends());
        Friends friends = friendsList.stream().filter(friend -> friend.getFriendUsersApp().equals(usersAppFriend)).findFirst().orElseThrow();
        usersApp.getFriends().remove(friends);
        friendsRepository.delete(friends);
        usersAppRepository.save(usersApp);
    }


}

