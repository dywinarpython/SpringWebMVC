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
@Schema(description = "Сущность ленты сообщества")
public class PostsCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    @Column(length = 30)
    private String title;

    @Column(length = 280)
    private String description;

    @CreationTimestamp
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Community community;

    public void setCommunity(Community community) {
        this.community = community;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<PostsCommunityFile> files;

    public void generateName() {
        this.name = "posts_" + UUID.randomUUID();
    }

}
