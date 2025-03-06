package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long>{
    List<Users> findByName(String name);
}
