package com.my.mvc.project.mymvcproject.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.my.mvc.project.mymvcproject.model.User;
import com.my.mvc.project.mymvcproject.model.UserDetails;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDetails(UserDetails details);

    @Query("select u from User u where u.details = ?1")
    List<User> findByUserName(String userName);
}
