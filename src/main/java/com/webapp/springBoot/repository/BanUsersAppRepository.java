package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.BanUsersApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanUsersAppRepository extends JpaRepository<BanUsersApp, Long> {
}
