package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Setter
@Schema(description = "Сущность пользователя")
@Entity
public class UsersApp {

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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name ="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();



    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "userOwner", cascade = CascadeType.ALL)
    private List<Community> community;

    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    private ImagesUsersApp imageUrl;

    @OneToMany(mappedBy = "usersApp", cascade = CascadeType.ALL)
    private List<PostsUserApp> postUserAppList;


    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    private BanUsersApp banUsersApp;

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
