package com.webapp.springBoot.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Schema(description = "Сущность ленты пользователя")
public class PostsUserApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    @Column(length = 30)
    private String title;

    @NotNull
    @Column(length = 280)
    private String description;

    @CreationTimestamp
    private LocalDateTime createDate;


    private LocalDateTime updateDate;

    @NotNull
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private UsersApp usersApp;

    public void setUsersApp(UsersApp usersApp) {
        this.usersApp = usersApp;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private List<PostsUserAppFile> files;

    public List<PostsUserAppFile> getFIle() {
        return files;
    }

    public void setListFile(List<PostsUserAppFile> listFile) {
        this.files = listFile;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }


}
