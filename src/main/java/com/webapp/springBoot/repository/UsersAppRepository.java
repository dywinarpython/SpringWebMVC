package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.UsersApp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UsersAppRepository extends JpaRepository<UsersApp, Long>{
    List<UsersApp> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<UsersApp> findByAgeBetween(int ageOne, int ageTwo, Pageable pageable);
    List<UsersApp> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname,  Pageable pageable);
    Optional<UsersApp> findByNickname(String nickname);
    Optional<UsersApp> findByEmail(String email);
    Optional<UsersApp> findByPhoneNumber(String phoneNumber);

    @Query(
            """
            SELECT id
            FROM UsersApp u
            WHERE u.nickname = :nickname
            """
    )
    Optional<Long> getUserIdByNickname(String nickname);

}
