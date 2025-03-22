package com.webapp.springBoot.repository;


import java.util.List;
import java.util.Optional;

import com.webapp.springBoot.entity.PostsUserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsUsersAppRepository extends JpaRepository<PostsUserApp, Long> {
    List<PostsUserApp> findByTitleContainingIgnoreCase(String title);
    Optional<PostsUserApp> findByName(String name);
}
