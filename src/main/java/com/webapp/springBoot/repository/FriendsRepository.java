package com.webapp.springBoot.repository;

import com.webapp.springBoot.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendsRepository extends JpaRepository<Friends, Long> {

    @Query(
            """
                 SELECT COUNT(f) > 0
                 FROM friends f
                 WHERE (f.usersApp.id = :id1 AND f.friendUsersApp.id = :id2) OR
                 (f.usersApp.id = :id2 AND f.friendUsersApp.id = :id1)
           """
    )
    boolean checkFriends(@Param("id1") Long id1, @Param("id2") Long id2);


    @Query(
            """
                 SELECT f
                 FROM friends f
                 WHERE (f.usersApp.id = :id)
           """
    )
    List<Friends> getFriends(@Param("id") Long id);
}
