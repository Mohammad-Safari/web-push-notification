package com.my.mvc.project.mymvcproject.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.mvc.project.mymvcproject.model.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    List<UserDetails> findByUsername(String username);

    List<UserDetails> findByEmail(String email);
}
