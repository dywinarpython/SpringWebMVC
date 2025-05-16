package com.webapp.springBoot.repository;

import com.webapp.springBoot.DTO.Friend.ResponseFriendDTO;
import com.webapp.springBoot.entity.Followers;
import com.webapp.springBoot.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowersRepository extends JpaRepository<Followers, Long> {
    @Query(
            """
                 SELECT COUNT(f) > 0
                 FROM Followers f
                 WHERE f.community.nickname = :nickname AND f.usersApp.id = :id
           """
    )
    boolean checkFollowers(@Param("id") Long id, @Param("nickname") String nickname);


    @Query(
            """
                 SELECT new com.webapp.springBoot.DTO.Friend.ResponseFriendDTO(
                  u.nickname,
                  u.name,
                  u.surname,
                  i.nameImage
                  )
                 FROM Followers f
                 JOIN f.usersApp u
                 JOIN f.community c
                 LEFT JOIN u.imageUrl i
                 WHERE c.nickname = :nickname
           """
    )
    List<ResponseFriendDTO> getFollowers(@Param("nickname") String nickname);

    @Query("""
            SELECT f
            FROM Followers f
            JOIN f.usersApp u
            JOIN f.community c
            WHERE c.nickname = :nicknameCommunity AND u.nickname = :nickname
            """)
    Optional<Followers> findFollowers(String nickname, String nicknameCommunity);

    List<Followers> findByCommunityNickname(String nickname);
}
