package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User,Integer> {

    public List<User> findByEmail(String email);
    
}
