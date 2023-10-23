package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user")User user, BindingResult bindingResult,@RequestParam(value="agreement",defaultValue = "false")boolean agreement,Model model) {
        try {
            // System.out.println(user);
            // System.out.println(agreement);
            if(!agreement) {
                throw new Exception("You have not agreed the terms and conditions");
            }

            // System.out.println("Binding Result error printing "+bindingResult.hasErrors());
            if(bindingResult.hasErrors()) {
                System.out.println(bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");


            model.addAttribute("user",user);
            // System.out.println("I am Printing "+this.userRepository.findByEmail(user.getEmail()));
            if(this.userRepository.findByEmail(user.getEmail()).size()>0) {
                throw new Exception("You have already registered try different Email");
            }

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            this.userRepository.save(user);
            // System.out.println(resultUser);

            model.addAttribute("user",new User());
            model.addAttribute("message", new Message("You Successfully Registered !!","Thanks","alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            model.addAttribute("message", new Message("Something Went Wrong !!"+e.getMessage(),"Sorry","alert-danger"));
        }
        return "signup";
    }
}
