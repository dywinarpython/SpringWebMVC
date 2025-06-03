package com.webapp.springBoot.repository;

import com.webapp.springBoot.DTO.UserReaction.UserReactionDTO;
import com.webapp.springBoot.entity.UserPostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPostReactionRepository extends JpaRepository<UserPostReaction, Long> {

    Optional<UserPostReaction> findByUsersApp_IdAndNamePost(Long userId, String namePost);

    @Query("""
            select new com.webapp.springBoot.DTO.UserReaction.UserReactionDTO(u.namePost,u.rating)
            from UserPostReaction u
            where u.usersApp.nickname = :nickname and u.namePost in :namePost
            """)
    List<UserReactionDTO> findByUsersApp_NicknameAndNamePost(String nickname, List<String> namePost);

    @Modifying
    @Query(value = """
        WITH updated AS (
          UPDATE posts_community
             SET rating = rating + :rating
           WHERE name = :postName
           RETURNING 1
        )
        UPDATE posts_user_app
           SET rating = rating + :rating
         WHERE name = :postName
           AND NOT EXISTS (SELECT 1 FROM updated)
        """, nativeQuery = true)
    void updateRatingForPost(@Param("postName") String postName,
                            @Param("rating")   Integer rating);

    boolean existsByUsersApp_IdAndNamePost(Long userId, String namePost);


    @Query(value = """
            select count(*) = 0
            from users_app ua
            join posts_user_app pua on pua.users_app_id = ua.id
            where ua.nickname = :nickname and pua."name" = :namePost
            """, nativeQuery = true)
    boolean checkCreator(@Param("nickname") String nicknameUser, @Param("namePost") String namePost);


    List<UserPostReaction> findByNamePost(String namePost);
}
