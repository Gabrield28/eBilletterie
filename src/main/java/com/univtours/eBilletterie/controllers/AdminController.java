package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.List;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController extends BaseController {

    @Autowired
    EventRepository eventRepo;
    
    @GetMapping("/admin")
    public String adminIndex(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "TicketMaster Admin", principal, "inAdmin");
        
        if((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        int LIMIT = 5;
        
        List<User> users = userRepo.findAll();
        // If number of users is too great, limit that number to LIMIT
        if (users.size() > LIMIT) {
            users = users.subList(users.size()-LIMIT, users.size());
        }
		model.addAttribute("users", users);
        
        List<Event> events = eventRepo.findAll();
        // If number of events is too great, limit that number to LIMIT
        if (events.size() > LIMIT) {
            events = events.subList(events.size()-LIMIT, events.size());
        }
		model.addAttribute("events", events);
		
		return "admin";
	}
}
