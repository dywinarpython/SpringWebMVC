package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.ImagesCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImagesCommunityRepository extends JpaRepository<ImagesCommunity, Long>  {
    Optional<ImagesCommunity> findByNameImage(UUID nameImage);
}
