package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.PostsUserAppFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePostsUsersAppRepository extends JpaRepository<PostsUserAppFile, Long> {
    Optional<PostsUserAppFile> findByNameFile(String nameFile);
}
