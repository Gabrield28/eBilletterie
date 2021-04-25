package com.univtours.eBilletterie.controllers;

import java.security.Principal;

import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class BaseController {

	@Autowired
	UserRepository userRepo;
    
    protected Model fillModel(Model model, String title, Principal principal) {
        return fillModel(model, title, principal, "");
    }
    
    protected Model fillModel(Model model, String title, Principal principal, String context) {
        if (principal != null) {
            User user = userRepo.findByUsername(principal.getName());
			model.addAttribute("principal", principal);
			model.addAttribute("isLoggedIn", true);
			model.addAttribute("user", user);
			model.addAttribute("hasRoleAdmin", user.hasRole("ROLE_ADMIN"));
		}
        model.addAttribute("title", title);
        if (context.equals("inAdmin")) {
            model.addAttribute("inAdmin", true);
        }
        return model;
    }
}
