package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Feed.FeedDTO;
import com.webapp.springBoot.DTO.Feed.ListFeedDTO;
import com.webapp.springBoot.DTO.Post.ResponseListPostDTO;
import com.webapp.springBoot.DTO.Post.ResponsePostDTO;
import com.webapp.springBoot.repository.FeedRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FeedService {



    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PostUsersAppService postUsersAppService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private PostCommunityService postCommunityService;

    @Value("${size.feed}")
    private Integer size;

    public ResponseListPostDTO getFeed(String nickname, Integer page) {
        Long userId = usersAppRepository.getUserIdByNickname(nickname).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с таким nickname нет"));
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        Cache cachePost = cacheManager.getCache("POST");
        if(cache == null || cachePost == null){
            throw new RuntimeException("Кеш не доступен");
        }
        ListFeedDTO listFeedDTO = cache.get(userId + ":" + page, ListFeedDTO.class);
        List<FeedDTO> feed;
        if(listFeedDTO == null){
            feed = feedRepository.findByUserIdOrderByCreateTimeDesc(userId, PageRequest.of(page, size));
            // нужен прогрев кеша
        } else{
            feed = listFeedDTO.getFeedList();
        }
        List<ResponsePostDTO> responsePostDTOS = new ArrayList<>();
        feed.forEach(fd -> {
            ResponsePostDTO responsePostDTO = cachePost.get(fd.getNamePost(), ResponsePostDTO.class);
            if(responsePostDTO == null){
                try{
                    responsePostDTO = postUsersAppService.getPost(fd.getNamePost());
                } catch (NoSuchElementException e){
                    responsePostDTO = postCommunityService.getPost(fd.getNamePost());
                }
            }
                    responsePostDTOS.add(responsePostDTO);
                }
        );
        return new ResponseListPostDTO(responsePostDTOS);

    }

}
