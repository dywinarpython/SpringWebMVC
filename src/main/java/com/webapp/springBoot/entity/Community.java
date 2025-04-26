package com.webapp.springBoot.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor

@Setter
@Schema(description = "Сущность сообщества")
@Entity
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Getter
    @NotNull
    @Column(length = 20)
    private String name;

    @Getter
    @NotNull
    @Column(length = 20, unique = true)
    private String nickname;

    @Getter
    @NotNull
    private String description;


    private Long countUser;

    @Getter
    @NotNull
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private UsersApp userOwner;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ImagesCommunity imageUrl;

    @Getter
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<PostsCommunity> postsCommunityList;

    public void setPostCommunityList(PostsCommunity postsCommunity) {
        postsCommunityList.add(postsCommunity);
    }

    public List<PostsCommunity> getPostsUserAppList() {
        return postsCommunityList;
    }

    public Long getCountUser() {
        return countUser;
    }

    public ImagesCommunity getImageUrlId() {
        return imageUrl;
    }

    public Community(UsersApp userOwnerId, String description, String name, String nickname) {
        this.userOwner = userOwnerId;
        this.description = description;
        this.name = name;
        this.nickname = nickname;
    }
}
