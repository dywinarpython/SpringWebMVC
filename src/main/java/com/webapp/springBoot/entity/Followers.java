package com.webapp.springBoot.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "followers_id")
    private UsersApp usersApp;

    @OneToOne
    @JoinColumn(name = "community_id")
    private Community community;
}
