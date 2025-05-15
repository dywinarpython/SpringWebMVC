package com.webapp.springBoot.repository;

import com.webapp.springBoot.DTO.Feed.FeedDTO;
import com.webapp.springBoot.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query("SELECT  new com.webapp.springBoot.DTO.Feed.FeedDTO\n(f.userId, f.namePost) FROM Feed f WHERE f.userId = :id ORDER BY f.createTime DESC")
    List<FeedDTO> findByUserIdOrderByCreateTimeDesc(@Param("id") Long id, Pageable pageable);
}
