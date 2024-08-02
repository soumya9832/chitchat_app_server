package com.soumya.chatserver.repository;

import com.soumya.chatserver.entity.User;
import com.soumya.chatserver.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    List<User> findAllByStatus(Status status);
    User findByUserName(String userName);
}
