package com.smart.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
    @Query("select c from Contact c where c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);

    @Query("select c from Contact c where c.user.id = :userId")
    public List<Contact> findContactsByUser(@Param("userId") int userId);


}
