package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

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

    // open add contact  form
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // process add contact
    @PostMapping("/process_contact")
    public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile multipartFile,Principal principal,Model model) {
        // System.out.println("Adding Contact....");
        // System.out.println(contact);
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            // System.out.println("Logged User Details :- "+user);
            if(multipartFile.isEmpty()) {
                System.out.println("Image File is Empty");
            } else {
                // String extension = Files.
                String newName = "IMG-" + System.currentTimeMillis() + "." + multipartFile.getContentType().split("/")[1];
                contact.setImageUrl(newName);

                System.out.println(newName);
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+newName);
                Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

                // System.out.println("Image is Uploaded");
                
            }
            
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            
            model.addAttribute("message", new Message("Added Successfully !! Add More.....",contact.getFirstName()+" "+contact.getLastName(),"alert-success"));
            // model.addAttribute("message", new Message("Added Successfully", "success", "Congrat"));
            // System.out.println("Added to DataBase");

        } catch (Exception e) {
            System.out.println("Error :- "+e.getMessage());
            model.addAttribute("message", new Message("Something Error !! Try Again.....","Sorry","alert-danger"));
        }

        return "normal/add_contact_form";
    }

}
