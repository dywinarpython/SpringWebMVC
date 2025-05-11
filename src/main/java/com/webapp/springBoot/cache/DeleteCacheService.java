package com.webapp.springBoot.cache;


import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.repository.FriendsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeleteCacheService {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private FriendsRepository friendsRepository;

    public void deleteAllCacheFriend(Long id) {
        List<Friends> friendsList = friendsRepository.findByUsersAppId(id);
        Cache cache = cacheManager.getCache("FRIENDS_LIST");
        if (cache == null) {
            log.error("Кеш не доступен, а именно: {}", "FRIENDS_LIST");
            return;
        }
        friendsList.forEach(friend -> {
            cache.evict(friend.getFriendUsersApp().getNickname());
        });
    }

}