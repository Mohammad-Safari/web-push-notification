package com.my.mvc.project.mymvcproject.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.model.User;

@Repository
public interface EventRepository extends JpaRepository<Event, User> {
    @Query("select e from Event e where e.receiver = ?1")
    List<Event> findByReceiver(User receiver);
}
