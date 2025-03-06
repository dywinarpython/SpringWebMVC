package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long>{
    List<Users> findByName(String name);
    List<Users> getUsersByAgeBetween(int ageOne, int ageTwo);
    List<Users> findByNameAndSurname(String name, String surname);


}
