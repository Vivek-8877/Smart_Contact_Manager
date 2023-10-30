package com.smart.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CONTACT")
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    private String work;

    @Column(unique = true)
    private String email;
    private String phone;
    private String imageUrl;

    @Column(length = 50000)
    private String description;

    @ManyToOne
    private User user;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getWork() {
        return work;
    }
    public void setWork(String work) {
        this.work = work;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    // @Override
    // public String toString() {
    //     return "Contact [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", work=" + work
    //             + ", email=" + email + ", phone=" + phone + ", imageUrl=" + imageUrl + ", description=" + description
    //             + ", user=" + user + "]";
    // }
    
    
}
