package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Community;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByNameLike( String nickname);
    Optional<Community> findByNickname(String nickname);

}
