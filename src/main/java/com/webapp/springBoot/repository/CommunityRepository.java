package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByNameContainsIgnoreCaseOrderByName(String nickname, Pageable pageable);
    Optional<Community> findByNickname(String nickname);
    List<Community> findByOrderByName(Pageable pageable);

    @Query(value = """
            SELECT id
            FROM community
            WHERE nickname = :nickname
            """, nativeQuery = true)
    Optional<Long> getIdCommunity(@Param("nickname") String nickname);
}
