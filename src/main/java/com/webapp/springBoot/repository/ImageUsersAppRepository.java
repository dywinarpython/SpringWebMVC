package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.ImagesCommunity;
import com.webapp.springBoot.entity.ImagesUsersApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageUsersAppRepository extends JpaRepository<ImagesUsersApp, Long> {
    Optional<ImagesUsersApp> findByNameImage(String nameImage);
}
