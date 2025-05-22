package com.webapp.springBoot.cache;


import com.webapp.springBoot.entity.Followers;
import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.repository.FollowersRepository;
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
    @Autowired
    private FollowersRepository followersRepository;

    public void deleteAllCacheFriend(Long id) {
        List<Friends> friendsList = friendsRepository.findByUsersAppId(id);
        Cache cache = cacheManager.getCache("FRIENDS_LIST");
        Cache cache2 = cacheManager.getCache("CHECK_FRIEND");
        if (cache == null ||cache2 == null) {
            throw new RuntimeException("Кеш не доступен, а именно: FRIENDS_LIST или CHECK_FRIEND");
        }
        friendsList.forEach(friend -> {
            cache.evict(friend.getFriendUsersApp().getNickname());
            String nicknameUsers = friend.getUsersApp().getNickname();
            String nicknameFriends = friend.getFriendUsersApp().getNickname();
            cache2.evict(nicknameUsers.compareTo(nicknameFriends) < 0 ? nicknameUsers + '_' + nicknameFriends : nicknameFriends + '_' + nicknameUsers);
        });
    }

    public void deleteAllFolowersCache(String  nickname) {
        List<Followers> followersList = followersRepository.findByCommunityNickname(nickname);
        Cache cache = cacheManager.getCache("FOLLOWERS_LIST");
        if (cache == null) {
            throw new RuntimeException("Кеш не доступен, а именно: FOLLOWERS_LIST");
        }
        followersList.forEach(followers -> {
            cache.evict(nickname + ":" + followers.getUsersApp().getNickname());
        });
        followersRepository.deleteAll(followersList);
    }

}