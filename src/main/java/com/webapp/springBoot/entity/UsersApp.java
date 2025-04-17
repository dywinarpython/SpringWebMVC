package com.webapp.springBoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.management.relation.Role;
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

    @NotNull
    @Column(length = 15)
    private String name;

    @NotNull
    @Column(length = 20)
    private String surname;


    @NotNull
    private int age;

    @NotNull
    @Column(length = 20, unique = true)
    private String nickname;

    @NotNull
    @Column(length = 60)
    private String password;


    @Column(length = 12)
    private String phoneNumber;

    @Column(unique = true)
    @NotNull
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name ="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();



    @JsonIgnore
    @OneToMany(mappedBy = "userOwner", cascade = CascadeType.ALL)
    private List<Community> community;

    @OneToOne(cascade = CascadeType.ALL)
    private ImagesUsersApp imageUrl;

    public BanUsersApp getBanUsersApp() {
        return banUsersApp;
    }

    @OneToMany(mappedBy = "usersApp", cascade = CascadeType.ALL)
    private List<PostsUserApp> postUserAppList;


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

    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Community> getCommunity() {
        return community;
    }

    public ImagesUsersApp getImageUrl() {
        return imageUrl;
    }
    public String getPassword() {
        return password;
    }
    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles.add(roles);
    }
    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }
}
