package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String name = principal.getName();
        User userByUserName = userRepository.getUserByUserName(name);
        model.addAttribute("user", userByUserName);
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashBoard(Model model,Principal principal) {
        model.addAttribute("title", "User DashBoard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add_contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }
}
