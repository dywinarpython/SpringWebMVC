package com.webapp.springBoot.repository;


import java.util.List;
import java.util.Optional;

import com.webapp.springBoot.entity.PostsCommunity;
import com.webapp.springBoot.entity.PostsUserApp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostsUsersAppRepository extends JpaRepository<PostsUserApp, Long> {
    List<PostsUserApp> findByTitleContainingIgnoreCase(String title);
    Optional<PostsUserApp> findByName(String name);

    @Query("SELECT p FROM PostsUserApp p WHERE p.usersApp.id = :userId")
    List<PostsUserApp> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
