package com.webapp.springBoot.repository;

import com.webapp.springBoot.DTO.Friend.ResponseFriendDTO;
import com.webapp.springBoot.entity.Friends;
import com.webapp.springBoot.entity.UsersApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {

    @Query(
            """
                 SELECT COUNT(f) > 0
                 FROM Friends f
                 WHERE (f.usersApp.id = :id1 AND f.friendUsersApp.id = :id2) OR
                 (f.usersApp.id = :id2 AND f.friendUsersApp.id = :id1)
           """
    )
    boolean checkFriends(@Param("id1") Long id1, @Param("id2") Long id2);


    @Query(
            """
                 SELECT new com.webapp.springBoot.DTO.Friend.ResponseFriendDTO(
                  friend.nickname,
                  friend.name,
                  friend.surname,
                  i.nameImage
                  )
                 FROM Friends f
                 JOIN f.usersApp u
                 JOIN f.friendUsersApp friend
                 LEFT JOIN friend.imageUrl i
                 WHERE u.id = :id
           """
    )
    List<ResponseFriendDTO> getFriends(@Param("id") Long id);
    List<Friends> findByUsersAppId(Long id);
}
