package com.smart.dao;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
    @Query("select c from Contact c where c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);

    @Query("select c from Contact c where c.user.id = :userId")
    public List<Contact> findContactsByUser(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Contact c WHERE c.id = :c_Id")
    void deleteByIdDefault(@Param("c_Id") Integer c_Id);
}
