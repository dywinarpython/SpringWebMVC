package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.ImagesCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagesCommunityRepository extends JpaRepository<ImagesCommunity, Long>  {
    Optional<ImagesCommunity> findByNameImage(String nameImage);
}
