package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersApp, Long>{
    List<UsersApp> findByNameContaining(String name);
    List<UsersApp> getUsersByAgeBetween(int ageOne, int ageTwo);
    List<UsersApp> findByNameContainingAndSurnameContaining(String name, String surname);
    Optional<UsersApp> findByNickname(String nickname);

}
