package com.webapp.springBoot.repository;


import com.webapp.springBoot.entity.PostsCommunity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostsCommunityRepository extends JpaRepository<PostsCommunity, Long> {
    List<PostsCommunity> findByTitleContainingIgnoreCase(String title);
    Optional<PostsCommunity> findByName(String name);
    @Query("SELECT p FROM PostsCommunity p WHERE p.community.id = :communityId")
    List<PostsCommunity> findByCommunityId(@Param("communityId") Long communityId, Pageable pageable);
}
