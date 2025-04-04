package com.webapp.springBoot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Сущность сохраненения банов пользователя")
@Entity(name = "ban_users_app")
public class BanUsersApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long timeBan;

    private boolean banForEver;

}
