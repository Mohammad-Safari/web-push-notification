package com.my.mvc.project.mymvcproject.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.mvc.project.mymvcproject.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
