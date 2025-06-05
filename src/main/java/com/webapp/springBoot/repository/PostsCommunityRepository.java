package com.webapp.springBoot.repository;


import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsUserApp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostsCommunityRepository extends JpaRepository<PostsCommunity, Long> {
    List<PostsCommunity> findByTitleContainingIgnoreCase(String title);
    Optional<PostsCommunity> findByName(String name);
    @EntityGraph(attributePaths = {"community"})
    @Query("SELECT p FROM PostsCommunity p WHERE p.community.nickname = :nickname")
    List<PostsCommunity> findByCommunityNickname(@Param("nickname")String nickname, Pageable pageable);

    @EntityGraph(attributePaths = {"community", "files"})
    @Query(
            """
            select u
            from PostsCommunity u
            where u.name in :namePosts
            """
    )
    List<PostsCommunity> findPostByNamePosts(@Param("namePosts") List<String> namePosts);
}
