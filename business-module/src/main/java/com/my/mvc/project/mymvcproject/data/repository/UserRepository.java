package com.my.mvc.project.mymvcproject.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.mvc.project.mymvcproject.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
