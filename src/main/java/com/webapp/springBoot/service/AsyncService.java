package com.webapp.springBoot.service;


import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.FriendsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AsyncService {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private FriendsRepository friendsRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void deleteAllCacheFriend(Long id){
        List<Friends> friendsList = friendsRepository.findByUsersAppId(id);
        Cache cache = cacheManager.getCache("FRIENDS_LIST");
        if(cache == null){
            log.warn("Кеш не доступен, а именно: {}", "FRIENDS_LIST");
            return;
        }
        friendsList.forEach(friend -> {
            cache.evict(friend.getFriendUsersApp().getNickname());
        });
    }
}
