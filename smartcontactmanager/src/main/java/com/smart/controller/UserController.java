package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

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

                // System.out.println(newName);
                File saveFile = new ClassPathResource("static/img").getFile();
                // System.out.println(saveFile);
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+newName);
                // System.out.println(path);
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
        return "redirect:/user/"+contact.getId()+"/contact/0";
        // return "normal/add_contact_form";
    }


    // Show Contacts
    @GetMapping("/show_contacts/{pageNumber}")
    public String viewContacts(@PathVariable("pageNumber") Integer pageNumber ,Model model,Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        Integer pageSize = 5;  // per page number of contacts
        // 1st Ways

        // String userName = principal.getName();
        // User user = this.userRepository.getUserByUserName(userName);
        // List<Contact> contacts = user.getContacts();

        // 2nd Ways
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(pageNumber, pageSize,Sort.by("firstName").and(Sort.by("lastName")));
        Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
        

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // show one contact

    @GetMapping(value="/{customer_id}/contact/{page_number}")
    public String showContactDetails(@PathVariable("customer_id") Integer cId,@PathVariable("page_number") Integer pageNumber,Model model,Principal principal) {
        // System.out.println(cId);
        // System.out.println("Hiii I am fired");
        try {
            Optional<Contact> contactOptional = this.contactRepository.findById(cId);
            Contact contact = contactOptional.get();
            String userEmail = principal.getName();
            if(contact.getUser().getEmail().compareTo(userEmail)==0) {
                model.addAttribute("title", contact.getFirstName()+" - Details");
                model.addAttribute("contact", contact);
                model.addAttribute("currentPage", pageNumber);
            } else {
                model.addAttribute("title", "Invalid Try");
            }
        } catch (Exception e) {
            model.addAttribute("title", "Invalid Try");
        }
        return "normal/show_contact_details";
    }

    // delete contact

    @GetMapping("/delete/{customer_id}")
    public String deleteContact(@PathVariable("customer_id") Integer c_id,Model model,Principal principal) {
        try {
            String userEmail = principal.getName();
            User userByUserName = this.userRepository.getUserByUserName(userEmail);
            Contact contact = this.contactRepository.findById(c_id).get();
            
            if(userByUserName.getId()==contact.getUser().getId()) {
                System.out.println("Deleting in Process...........");
                // delete saved image
                if(contact.getImageUrl()!=null) {
                    File file = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(file.getAbsolutePath()+File.separator+contact.getImageUrl());
                    if(Files.exists(path)) Files.delete(path);
                }

                // delete contact
                // this.contactRepository.deleteByIdDefault(c_id);
                // model.addAttribute("message", new Message(contact.getFirstName()+"Successfully deleted", "", "alert-success"));
                System.out.println(userByUserName.getContacts());
                userByUserName.getContacts().remove(contact);
                this.userRepository.save(userByUserName);
                System.out.println(userByUserName.getContacts());

            } else {

            }
        } catch (Exception e) {

        }

        return "redirect:/user/show_contacts/0";
    }

    // contact update process...
    @PostMapping("/process_update_contact/{c_id}")
    public String processContactUpdate(@PathVariable("c_id") Integer c_id,Model model) {
        Contact contact = this.contactRepository.findById(c_id).get();
        model.addAttribute("contact", contact);
        model.addAttribute("title", "Update - "+contact.getFirstName()+" - details");
        return "normal/update_contact";
    }

    @PostMapping("/update_contact")
    public String updateContact(@ModelAttribute Contact contact,Principal principal,Model model,@RequestParam("profileImage") MultipartFile multipartFile) {
        try {
            User userByUserName = this.userRepository.getUserByUserName(principal.getName());
            String oldImageUrl = this.contactRepository.findById(contact.getId()).get().getImageUrl();
            if(multipartFile.isEmpty()) {
                contact.setImageUrl(oldImageUrl);
            } else {
                //removing image
                if(contact.getImageUrl()!=null) {
                    File file1 = new ClassPathResource("static/img").getFile();
                    Path path1 = Paths.get(file1.getAbsolutePath()+File.separator+contact.getImageUrl());
                    if(Files.exists(path1)) Files.delete(path1);
                }

                // adding image
                String newName = "IMG-" + System.currentTimeMillis() + "." + multipartFile.getContentType().split("/")[1];
                contact.setImageUrl(newName);

                // System.out.println(newName);
                File saveFile = new ClassPathResource("static/img").getFile();
                // System.out.println(saveFile);
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+newName);
                // System.out.println(path);
                Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
            }
            // System.out.println(contact.getLastName());
            contact.setUser(userByUserName);
            userByUserName.getContacts().remove(contact);
            // boolean contains = userByUserName.getContacts().contains(contact);
            // System.out.println(contains);
            userByUserName.getContacts().add(contact);
            // System.out.println(userByUserName.getContacts().contains(contact));
            this.userRepository.save(userByUserName);
        } catch (Exception e) {

        }
        return "redirect:/user/"+contact.getId()+"/contact/0";
    }

    // showing Profile of logged in User
    @GetMapping("/view_profile")
    public String showProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/view_profile";
    }
}
