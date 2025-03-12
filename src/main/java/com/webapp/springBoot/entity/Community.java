package com.webapp.springBoot.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.NoArgsConstructor;


@NoArgsConstructor

@Setter
@Schema(description = "Сущность сообщества")
@Entity
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 20, unique = true)
    private String nickname;

    @NotNull
    private String description;


    private Long countUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_owner_id",referencedColumnName = "id")
    private UsersApp userOwnerId;


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "image_url_id",referencedColumnName = "id")
//    private ImagesCommunity imageUrlId;

    public Community(UsersApp userOwnerId, String description, String name, String nickname) {
        this.userOwnerId = userOwnerId;
        this.description = description;
        this.name = name;
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    public Long getCountUser() {
        return countUser;
    }

    public UsersApp getUserOwnerId() {
        return userOwnerId;
    }

//    public ImagesCommunity getImageUrlId() {
//        return imageUrlId;
//    }
}
