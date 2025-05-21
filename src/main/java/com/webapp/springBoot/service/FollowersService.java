package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Friend.ListResponseFriendDTO;
import com.webapp.springBoot.DTO.Friend.ResponseFriendDTO;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.Followers;
import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.FollowersRepository;
import com.webapp.springBoot.repository.FriendsRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.example.RequestFollowersFeedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FollowersService {

    @Autowired
    private FollowersRepository followersRepository;


    @Autowired
    private UsersService usersService;

    @Autowired
    private ImageUsersAppService imageUsersAppService;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CacheManager cacheManager;

    @Qualifier("requestFollowerKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, RequestFollowersFeedDTO> kafkaTemplate;


    // Первое обязательно Principal
    public Boolean checkFollowers(String nickname, String nicknameCommunity) {
        Cache cache = cacheManager.getCache("CHECK_FOLLOWERS");
        assert cache != null;
        if(Boolean.TRUE.equals(cache.get(nickname + ":" + nicknameCommunity, Boolean.class))){
            return true;
        }
        return followersRepository.checkFollowers(usersService.getIdWithNickname(nickname), nicknameCommunity);
    }

    @Caching(
            put = {
                    @CachePut(value = "CHECK_FOLLOWERS", key = "#nickname + ':' + #nicknameCommunity")
            },
            evict = {
                    @CacheEvict(value = "FOLLOWERS_LIST", key="#nicknameCommunity")
            }
    )
    public Boolean createFollowers(String nickname, String nicknameCommunity) throws ValidationErrorWithMethod {
        UsersApp usersApp = usersService.findUsersByNickname(nickname);
        Community community = communityService.findCommunityByNickname(nicknameCommunity);
        Followers followers = new Followers();
        followers.setUsersApp(usersApp);
        followers.setCommunity(community);
        followersRepository.save(followers);
        kafkaTemplate.send("news-feed-topic-follower", null, new RequestFollowersFeedDTO(nickname, nicknameCommunity));
        return true;
    }

    @Cacheable(value = "FOLLOWERS_LIST", key = "#nicknameCommunity")
    @Transactional(readOnly = true)
    public ListResponseFriendDTO getAllFollowers(String nicknameCommunity){
        List<ResponseFriendDTO> friendsList = followersRepository.getFollowers(nicknameCommunity);
        return new ListResponseFriendDTO(friendsList);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "FOLLOWERS_LIST", key="#nicknameCommunity"),
                    @CacheEvict(value = "CHECK_FOLLOWERS", key = "#nickname + ':' + #nicknameCommunity")
            }
    )
    @Transactional
    public void deleteFollowers(String nickname, String nicknameCommunity){
        Followers followers = followersRepository.findFollowers(nickname, nicknameCommunity).orElseThrow(() -> new NoSuchElementException("Пользователь не подписан на данное сообщество"));
        followersRepository.delete(followers);
        kafkaTemplate.send("news-feed-topic-follower-del", null, new RequestFollowersFeedDTO(nickname, nicknameCommunity));
    }
}

