package com.webapp.springBoot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Schema(description = "Сущность ленты пользователя")
public class PostsUserApp  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;


    @Column(length = 60)
    private String title;


    @Column(length = 280)
    private String description;

    @CreationTimestamp
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @Getter
    private Long rating;

    @Setter
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private UsersApp usersApp;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private List<PostsUserAppFile> files;

    public void generateName() {
        this.name = "posts_" + UUID.randomUUID();
    }

}
