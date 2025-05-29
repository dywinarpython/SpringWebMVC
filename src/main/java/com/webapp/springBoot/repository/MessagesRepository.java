package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Long> {


}
