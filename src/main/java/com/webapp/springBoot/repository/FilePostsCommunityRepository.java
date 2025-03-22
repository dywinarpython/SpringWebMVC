package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.PostsCommunityFile;
import com.webapp.springBoot.entity.PostsUserAppFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePostsCommunityRepository extends JpaRepository<PostsCommunityFile, Long> {
    Optional<PostsCommunityFile> findByNameFile(String nameFile);
}
