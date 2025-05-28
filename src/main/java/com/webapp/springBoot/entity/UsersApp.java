package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Setter
@Schema(description = "Сущность пользователя")
@Entity
public class UsersApp {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Column(length = 15)
    private String name;

    @Getter
    @NotNull
    @Column(length = 20)
    private String surname;


    @Getter
    @NotNull
    private int age;

    @Getter
    @NotNull
    @Column(length = 20, unique = true)
    private String nickname;

    @Getter
    @Setter
    @NotNull
    @Column(length = 60)
    private String password;


    @Column(length = 12, unique = true)
    private String phoneNumber;

    @Column(unique = true)
    @Email
    private String email;

    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name ="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();



    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "userOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Community> community;

    @Getter
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ImagesUsersApp imageUrl;

    @OneToMany(mappedBy = "usersApp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostsUserApp> postUserAppList;


    @Getter
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BanUsersApp banUsersApp;


    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usersApp", fetch = FetchType.LAZY)
    private List<Friends> friends;

    public void setPostUserAppList(PostsUserApp postsUserApp) {
        postUserAppList.add(postsUserApp);
    }

    public List<PostsUserApp> getPostsUserAppList() {
        return postUserAppList;
    }

    public UsersApp(String name, String surname, int age, String nickname, String password) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.nickname = nickname;
        this.password = password;

    }

    public void rolesAdd(Roles roles) {
        this.roles.add(roles);
    }

}
