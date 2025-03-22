package com.webapp.springBoot.repository;


import com.webapp.springBoot.entity.PostsCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostsCommunityRepository extends JpaRepository<PostsCommunity, Long> {
    List<PostsCommunity> findByTitleContainingIgnoreCase(String title);
    Optional<PostsCommunity> findByName(String name);
}
